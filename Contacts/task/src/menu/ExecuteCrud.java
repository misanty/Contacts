package menu;
import domain.Contact;
import domain.Organization;
import domain.Person;
import inputController.RepositoryCrud;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class ExecuteCrud {

    //TODO Fix nextline issues,
    private static Scanner scanner = new Scanner(System.in);
    private static ExecuteCrud executeCrudInstance;
    private final RepositoryCrud crud;
    private static HashMap<Integer, Contact> searchResult = new HashMap<>();
    private static Contact contact;

    public static ExecuteCrud getInstance(RepositoryCrud crud) {
        if (executeCrudInstance == null) {
            executeCrudInstance = new ExecuteCrud(crud);
        }
        return executeCrudInstance;
    }

    private ExecuteCrud(RepositoryCrud crud) {
        this.crud = crud;

    }

    protected void add() {
        //changed here
        String type = scanner.nextLine();
        Contact contact = null;
        switch (type.toLowerCase().strip()) {
            case "person" : contact = new Person();
            break;
            case "organization" : contact = new Organization();
            break;
            default : System.out.println("Bad record type!\n");
            break;

        }
        if (contact != null) {
            for (var name : contact.possibleFields()) {
                String value = readWriteFieldName(name);
                if (!contact.changeFieldValue(name, value)) {
                    System.out.println("Bad " + name + "!");
                }
            }
            crud.add(contact);
            serialize();
            System.out.println("The record added\n");
        }

    }

    protected void count() {
        System.out.printf("The Phone Book has %d records.\n", crud.count());
        System.out.println();
    }

    protected void serialize() {
        try {
            crud.serialize();
        } catch (IOException ignored) {
        }
    }

    protected void edit() {

        System.out.print("[record] Enter action (edit, delete, menu): ");

        String out = scanner.nextLine();
        switch (out) {
            case "edit":
                executeEdit();
                serialize();
                break;
            case "delete":
                executeDelete();
                serialize();
                break;
            case "menu":
                break;
            default:
                System.out.println("Wrong format!");
                break;
        }
        //  System.out.println();


    }

    private void executeDelete() {
        if (crud.remove(contact)) {
            System.out.println("Contact deleted!");
        } else {
            System.out.println("No contacts to delete!");
        }
    }

    protected void getFields() {
        String output = "Select a field (" +
                String.join(", ", contact.possibleFields()) +
                "):";
        System.out.println(output);
    }

    protected void executeEdit() {
        getFields();
        String fieldName = scanner.nextLine();

        if (contact.checkFieldName(fieldName)) {
            String value = readWriteFieldName(fieldName);
            contact.setFieldAndTime(fieldName, value);
        } else {
            System.out.println("Wrong " + fieldName + " format!");
        }
        System.out.println("Saved");
        System.out.println(contact.getDetailedInfo());
    }

    protected boolean list() {

        ListIterator<Contact> iterator = crud.getList().listIterator();
        int count = 1;

        while (iterator.hasNext()) {
            System.out.println(count + "." + " " + iterator.next().getShortInfo());
            count++;
        }
        if (crud.isEmpty()) {
            System.out.println("No records to show!\n");
            // System.out.println("\n");
            return false;
        }


        System.out.print("\n[list] Enter action ([number], back): ");
        String out = scanner.nextLine();
        if (out.matches("^\\d+$")) {

            contact = crud.findById(Integer.parseInt(out) - 1);
            if (contact != null) {
                System.out.println(contact.getDetailedInfo());
                //System.out.println("\n");
                edit();
            } else {
                System.out.println("Wrong id!");
            }
            return false;
        } else if ("back".equals(out)) {
            return false;
        }
        return true;

    }

    protected boolean search() {

        if (!crud.isEmpty()) {
            System.out.print("Enter search query: ");
            //Changed here
            List<Contact> results = crud.search(scanner.nextLine());

            if (results.size() > 0) {
                System.out.printf("Found %d results:%n", results.size());
                //iterateSearch(results.listIterator());
                iterateSearch(results);
            } else {
                System.out.println("Found 0 results");
                return false;
            }

            System.out.print("[search]Enter action ([number], back, again): ");
            //changed here
            String out = scanner.nextLine();
            switch (out.toLowerCase()) {
                case "back":
                    //System.out.println("\n");
                    return false;

                case "again":
                    search();
            }
            if (out.matches("^\\d+$")) {
                int id = Integer.parseInt(out);
                System.out.println(searchResult.get(id).getDetailedInfo());
                contact = searchResult.get(id);
                edit();
                return false;
            }

        } else {
            System.out.println("No records to search!\n");
        }
        return false;


    }

    private void iterateSearch(List<Contact> search) {
        for (int i = 1; i <= search.size(); i++) {
            searchResult.put(i, search.get(i - 1));
            System.out.println(i + "." + " " + search.get(i - 1).getShortInfo());
        }
        System.out.println();


    }
/*
    private void iterateSearch(ListIterator<Contact> search) {
        int count = 1;
        searchResult.clear();

        while (search.hasNext()) {
            searchResult.put(count, search.next());
            System.out.println(count + " - " + search.next().getShortInfo());
            count++;
        }
        System.out.println();


    }*/

    private String readWriteFieldName(String fieldName) {
        System.out.print("Enter " + fieldName + ": ");
        return scanner.nextLine();
    }


}
