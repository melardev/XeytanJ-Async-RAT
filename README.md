# WARNING
Development in progress, this is far from stable and well written, I hope one day to finish refactoring and
writing good looking code for this project. But I don't think to make this project production ready to avoid
abuse.
  
# Introduction
This was my first RAT I ever wrote, several years ago, I was too dumb at that time,
I had written working but really bad code, since then, when I could,
I refactored the code as I learnt new programming concepts and design patterns, but there
are still pieces of code I would not write nowadays and I have to 
refactor them in the future. So please forgive me if you see some nonsense.

This Xeytan RAT flavour uses Asynchronous programming, at this time, only Asynchronous sockets, but in the future
I will refactor the code to use other Async APIs such as NIO path monitoring.

# Features
- Asynchronous sockets
- Swing UI
- Desktop streaming
- Camera Streaming
- File Explorer
- Process List
- Reverse Shell
- Chat

# TODO
- Ui, has to be redesigned, it is just horrible right now
- Voice Recorder
- FileSystem UI has artifacts
- Fix Spring code. Currenty I am not sure if the App would work if I use Spring Service Locator.
- Port forwarder using upnp, there are many very good libraries that I can use as a reference:
    * https://github.com/4thline/cling
    * https://github.com/kaklakariada/portmapper
    * https://github.com/offbynull/portmapper
- Implement application Language switching
- Database code is incomplete and badly written, refactor it and add JPA code rather than JDBC.
- Keylogger(JNI, or executing external executables, I have some keylogger implementations in my Github already)
