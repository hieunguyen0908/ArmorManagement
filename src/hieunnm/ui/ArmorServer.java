/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hieunnm.ui;


import hieunnm.rmi.ArmorServerImplement;
import hieunnm.rmi.ArmorInterface;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class ArmorServer {

    private static final int PORT = 12340;
    private static Registry registry;

    public static void startRegistry() throws RemoteException {
        registry = LocateRegistry.createRegistry(PORT);
    }

    public static void registerObject(String name, Remote remoteObj) throws
            RemoteException, AlreadyBoundException {
        registry.bind(name, remoteObj);
        System.out.println("Registered: " + name + "->" + remoteObj.getClass().getName()
                + "[" + remoteObj + "]");
    }

    public static void main(String[] args) {

        try {
            System.out.println("Server starting....");
            startRegistry();
            registerObject(ArmorInterface.class.getSimpleName(), new ArmorServerImplement());
            System.out.println("Server started!");
        } catch (AlreadyBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
