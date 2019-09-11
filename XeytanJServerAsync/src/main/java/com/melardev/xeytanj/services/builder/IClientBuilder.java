package com.melardev.xeytanj.services.builder;

import com.melardev.xeytanj.models.BuildClientInfoStructure;
import com.melardev.xeytanj.services.IService;

public interface IClientBuilder extends IService {

    public boolean build(BuildClientInfoStructure buildClientInfoStructure);
}
