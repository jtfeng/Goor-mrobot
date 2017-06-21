package cn.muye.service;


import com.mpush.api.Client;

public interface ReceiveService {

    void analysis(Client client, byte[] content);
}
