package cn.muye.base.service;


import com.mpush.api.Client;

public interface ReceiveService {

    void analysis(Client client, byte[] content);
}
