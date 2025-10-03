import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.Scanner;


public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "Apurba@2003";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers loaded successfully");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an Option: ");
                int choice = sc.nextInt();
                switch (choice) {

                    case 1:
                        reserveRoom(connection, sc);
                        break;

                    case 2:
                        viewReservation(connection);
                        break;

                    case 3:
                        getRoomNumber(connection, sc);
                        break;

                    case 4:
                        updateReservation(connection, sc);
                        break;


                    case 5:
                        deleteReservation(connection, sc);
                        break;

                    case 0:
                        exit();
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid Choice. Try again..");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private static void reserveRoom(Connection connection, Scanner sc) {

        try {
            System.out.println("Enter Guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter Room Number: ");
            int roomNumber = sc.nextInt();
            System.out.println("Enter Guest Contact Number: ");
            String contactNumber = sc.next();


            String sql = "insert into Reservations (guest_name,room_number,contact_number)" +
                    "values('" + guestName + "'," + roomNumber + ",'" + contactNumber + "')";
            try (Statement stat = connection.createStatement()) {
                int rowsAffected = stat.executeUpdate(sql);

                if (rowsAffected > 0) {
                    System.out.println("Reservation Successfull!!");
                } else {
                    System.out.println("Reservation Failed!!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection) throws SQLException {

        String sql = "select reservation_id, guest_name, room_number, contact_number, reservation_date from Reservations";

        try (Statement stat = connection.createStatement();
             ResultSet resultSet = stat.executeQuery(sql)) {

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultSet.next()) {
                int reservation_id = resultSet.getInt("reservation_id");
                String guest_name = resultSet.getString("guest_name");
                int room_number = resultSet.getInt("room_number");
                String contact_number = resultSet.getString("contact_number");
                String reservation_date = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s  |\n",
                        reservation_id, guest_name, room_number, contact_number, reservation_date);
                System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            }

            


        }
    }

    private static void getRoomNumber(Connection connection, Scanner sc) {

        try {
            System.out.println("Enter Reservation ID: ");
            int reservationID = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Guest Name: ");
            String guestName = sc.next();


            String sql = "select room_number from Reservations " +
                    "where reservation_id = " + reservationID +
                    " AND guest_name = '" + guestName + "'";

            try (Statement stat = connection.createStatement();
                 ResultSet resultSet = stat.executeQuery(sql)) {

                if (resultSet.next()) {
                    int room_number = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationID +
                            " and Guest Name " + guestName + " is: " + room_number);
                } else {
                    System.out.println("Reservation not found for the given ID and Guest Name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner sc) {

        try {
            System.out.println("Enter Reservation ID to update: ");
            int reservationID = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(connection, reservationID)) {
                System.out.println("Reservation not found for the given ID: ");
                return;
            }

            System.out.println("Enter New Guest name: ");
            String newGuestName = sc.nextLine();
            System.out.println("Enter New Room Number: ");
            int newRoomNumber = sc.nextInt();
            System.out.println("Enter New  Contact Number: ");
            String newContactNumber = sc.next();


            String sql = "update Reservations set guest_name ='" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "', " +
                    "where reservation_id = " + reservationID;

            try (Statement stat = connection.createStatement()) {
                int rowsAffected = stat.executeUpdate(sql);

                if (rowsAffected > 0) {
                    System.out.println("Reservation Updated Successfull!!");
                } else {
                    System.out.println("Reservation Update Failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner sc) {

        try {
            System.out.println("Enter Reservation ID to delete: ");
            int reservationID = sc.nextInt();
            sc.nextLine();

            if (!reservationExists(connection, reservationID)) {
                System.out.println("Reservation not found for the given ID: ");
                return;
            }

            String sql = "delete from Reservations where reservation_id= " + reservationID;

            try (Statement stat = connection.createStatement()) {
                int rowsAffected = stat.executeUpdate(sql);

                if (rowsAffected > 0) {
                    System.out.println("Reservation Deleted Successfull!!");
                } else {
                    System.out.println("Reservation Deleted Failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationID) {

        try {
            String sql = "select reservation_id from Reservations where reservation_id = " + reservationID;

            try (Statement stat = connection.createStatement();
                 ResultSet resultSet = stat.executeQuery(sql)) {

                return resultSet.next();
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Existing System");
        int i=5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("Thanks For Using Hotel Reservation System!!");
    }
}