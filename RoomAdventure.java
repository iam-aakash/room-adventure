import java.util.Scanner;

// Name: Aakash Baral

/* My features:
1. Drop command: player can drop items back into the room
2. Inspect command lets examine items in your inventory for hidden info
3. USE command can make use of certain items in room
Using the battery in the Robotics Lab powers on a robot*/

public class RoomAdventure {

    private static Room currentRoom;
    private static String[] inventory = new String[6];
    private static String status;

    private static final String DEFAULT_STATUS =
        "I don't understand. Use GO / LOOK / TAKE / DROP / INSPECT / USE.";

    public static void main(String[] args) {

        setupGame();

        Scanner scan = new Scanner(System.in);

        while (true) {

            System.out.println(currentRoom);
            printInventory();

            System.out.print("\nWhat would you like to do? ");
            String input = scan.nextLine().trim().toLowerCase();

            if (input.equals("quit") || input.equals("exit"))
                break;

            String[] words = input.split(" ");

            if (words.length != 2) {
                status = DEFAULT_STATUS;
                System.out.println(status);
                continue;
            }

            String verb = words[0];
            String noun = words[1];

            switch (verb) {
                case "go":      handleGo(noun); break;
                case "look":    handleLook(noun); break;
                case "take":    handleTake(noun); break;
                case "drop":    handleDrop(noun); break;      // feature #1
                case "inspect": handleInspect(noun); break;   // feature #2
                case "use":     handleUse(noun); break;       // faeture #3
                default:
                    status = DEFAULT_STATUS;
            }

            System.out.println(status);
        }
    }

    //inventory

    private static void printInventory() {
        System.out.print("\nInventory: ");
        boolean empty = true;

        for (String item : inventory) {
            if (item != null) {
                System.out.print(item + " ");
                empty = false;
            }
        }
        if (empty) System.out.print("(empty)");
        System.out.println();
    }


    private static void handleGo(String direction) {
        status = "You can't go that way.";

        String[] dirs = currentRoom.getExitDirections();
        Room[] dests = currentRoom.getExitDestinations();

        for (int i = 0; i < dirs.length; i++) {
            if (direction.equals(dirs[i])) {
                currentRoom = dests[i];
                status = "You walk " + direction + ".";
                return;
            }
        }
    }

    private static void handleLook(String noun) {
        status = "You don't see that.";

        String[] items = currentRoom.getItems();
        String[] desc = currentRoom.getItemDescriptions();

        for (int i = 0; i < items.length; i++) {
            if (noun.equals(items[i])) {
                status = desc[i];
                return;
            }
        }
    }

    private static void handleTake(String noun) {
        status = "You can't take that.";

        String[] grabs = currentRoom.getGrabbables();

        for (String g : grabs) {
            if (noun.equals(g)) {

                // adding to inventory
                for (int i = 0; i < inventory.length; i++) {
                    if (inventory[i] == null) {
                        inventory[i] = noun;
                        currentRoom.removeGrabbable(noun);
                        status = "You picked up the " + noun + ".";
                        return;
                    }
                }

                status = "Inventory is full.";
                return;
            }
        }
    }

    // drop feature

    private static void handleDrop(String noun) {
        for (int i = 0; i < inventory.length; i++) {
            if (noun.equals(inventory[i])) {
                inventory[i] = null;
                currentRoom.addGrabbable(noun);
                status = "You dropped the " + noun + " on the ground.";
                return;
            }
        }
        status = "You don't have that.";
    }

    //inspect feature

    private static void handleInspect(String noun) {

        for (String item : inventory) {
            if (noun.equals(item)) {

                switch (noun) {
                    case "battery":
                        status = "A heavy-duty lab battery labeled: 'MODEL R-12'.";
                        break;
                    case "key":
                        status = "A small metal key engraved with 'SR-01'.";
                        break;
                    case "wire":
                        status = "A copper wire coil, slightly frayed.";
                        break;
                    case "manual":
                        status = "A robotics manual titled 'Beginner Robotics Troubleshooting'.";
                        break;
                    default:
                        status = "Nothing unusual about the " + noun + ".";
                }
                return;
            }
        }
        status = "You aren't carrying that.";
    }

    // USE feature

    private static void handleUse(String noun) {

        boolean hasItem = false;
        for (String item : inventory)
            if (noun.equals(item))
                hasItem = true;

        if (!hasItem) {
            status = "You don't have that to use.";
            return;
        }

        //Using battery in Robotics Lab effect
        if (noun.equals("battery") &&
            currentRoom.getName().equals("Robotics Lab")) {

            status = "You slot the battery into a small inactive robot.\n" +
                     "The robot whirs to life and projects a message:\n" +
                     "'TIP: Check the Storage Closet for something important.'";

            return;
        }

        //key in Server Room effect
        if (noun.equals("key") &&
            currentRoom.getName().equals("Server Room")) {

            status = "You turn the key in a hidden floor panel.\n" +
                     "A compartment opens revealing a loose wire.";
            currentRoom.addGrabbable("wire");
            return;
        }

        status = "Using the " + noun + " does nothing here.";
    }

    // setting up game

    private static void setupGame() {

        Room lobby = new Room("Lobby");
        Room robotics = new Room("Robotics Lab");
        Room server = new Room("Server Room");
        Room closet = new Room("Storage Closet");

        // LOBBY
        lobby.setExitDirections(new String[]{"east", "south"});
        lobby.setExitDestinations(new Room[]{robotics, closet});
        lobby.setItems(new String[]{"directory", "bench"});
        lobby.setItemDescriptions(new String[]{
            "A digital directory displaying a map of Innovation Hall.",
            "A cold metal bench under a flickering light."
        });
        lobby.setGrabbables(new String[]{"manual"});

        // ROBOTICS LAB
        robotics.setExitDirections(new String[]{"west", "south"});
        robotics.setExitDestinations(new Room[]{lobby, server});
        robotics.setItems(new String[]{"robot", "toolbox"});
        robotics.setItemDescriptions(new String[]{
            "A small robot with an empty battery compartment.",
            "A dusty toolbox full of random screws."
        });
        robotics.setGrabbables(new String[]{"battery"});

        // SERVER ROOM
        server.setExitDirections(new String[]{"north", "west"});
        server.setExitDestinations(new Room[]{robotics, closet});
        server.setItems(new String[]{"rack", "console"});
        server.setItemDescriptions(new String[]{
            "A server rack humming loudly.",
            "A locked console displaying error codes."
        });
        server.setGrabbables(new String[]{"key"});

        // STORAGE CLOSET
        closet.setExitDirections(new String[]{"north", "east"});
        closet.setExitDestinations(new Room[]{lobby, server});
        closet.setItems(new String[]{"crates", "shelf"});
        closet.setItemDescriptions(new String[]{
            "Stacks of old crates covered in dust.",
            "A shelf filled with outdated equipment manuals."
        });
        closet.setGrabbables(new String[]{});

        currentRoom = lobby;
        status = "You enter Innovation Hall late at night. It's unusually quiet...";
    }
}


class Room {

    private String name;
    private String[] exitDirections;
    private Room[] exitDestinations;
    private String[] items;
    private String[] itemDescriptions;
    private String[] grabbables;

    public Room(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void setExitDirections(String[] dirs) { exitDirections = dirs; }
    public void setExitDestinations(Room[] dest) { exitDestinations = dest; }
    public void setItems(String[] it) { items = it; }
    public void setItemDescriptions(String[] desc) { itemDescriptions = desc; }
    public void setGrabbables(String[] g) { grabbables = g; }

    public String[] getExitDirections() { return exitDirections; }
    public Room[] getExitDestinations() { return exitDestinations; }
    public String[] getItems() { return items; }
    public String[] getItemDescriptions() { return itemDescriptions; }
    public String[] getGrabbables() { return grabbables; }

    public void addGrabbable(String item) {
        String[] newArr = new String[grabbables.length + 1];
        for (int i = 0; i < grabbables.length; i++)
            newArr[i] = grabbables[i];
        newArr[grabbables.length] = item;
        grabbables = newArr;
    }

    public void removeGrabbable(String item) {
        int count = 0;
        for (String g : grabbables)
            if (!g.equals(item))
                count++;

        String[] newArr = new String[count];
        int index = 0;
        for (String g : grabbables)
            if (!g.equals(item))
                newArr[index++] = g;

        grabbables = newArr;
    }

    public String toString() {

        String result = "\nLocation: " + name;

        result += "\nItems: ";
        if (items != null)
            for (String i : items)
                result += i + " ";

        result += "\nGrabbables: ";
        if (grabbables != null)
            for (String g : grabbables)
                result += g + " ";

        result += "\nExits: ";
        if (exitDirections != null)
            for (String d : exitDirections)
                result += d + " ";

        return result + "\n";
    }

}
