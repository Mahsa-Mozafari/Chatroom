
import Client.Client;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("enter your name: ");
        String name = sc.nextLine();

        Client client=new Client(name);
        client.start();

    }
}