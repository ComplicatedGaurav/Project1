import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

class HotelReservationSystem{
    private static final String url="jdbc:mysql://localhost:3306/hotel_db";
    private static final String username="root";
    private static final String password="Gaurav@1211";
    public static void main(String[] args)throws ClassNotFoundException,SQLException,InterruptedException{


        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());

        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {
                System.out.println();
                System.out.println("Hotel Reservation System !!");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1.Reserve a Room");
                System.out.println("2.View Reservation");
                System.out.println("3.Get Reservation");
                System.out.println("4.Update Reservation");
                System.out.println("5.Delete Reservation");
                System.out.println("0.Exit");
                System.out.println("Enter Your Choice");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection,scanner);

                        break;
                    case 4:
                        updateReservation(connection,scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;


                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void reserveRoom(Connection connection,Scanner scanner){
        try {
            System.out.println("Enter Guest Name");

            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter Guest Room Number");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter Guest Contact number");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation Failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }
    private  static void viewReservation(Connection connection)throws SQLException{
        String sql="SELECT reservationId, guestName,roomNumber,contactNumber,RESERVATIONDATE FROM RESERVATIONS";
        try(Statement statement=connection.createStatement()){
            ResultSet rs=statement.executeQuery(sql);
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            while (rs.next()){
                int reservationId= rs.getInt("reservationId");
                String guestName=rs.getString("guestName");
                int roomNumber= rs.getInt("roomNumber");
                String contactNumber= rs.getString("contactNumber");
                String reservationdate= rs.getString("reservationdate").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationdate);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }

    }
    private static void getRoomNumber(Connection connection,Scanner scanner) throws SQLException {
        System.out.println("Enter Guest Name");
        String guestName = scanner.next();
        scanner.nextLine();
        System.out.println("Enter Reservation Id");
        int reservationId = scanner.nextInt();

        String sql = "SELECT roomNumber FROM reservations" +
                "WHERE reservation_id = " + reservationId +
                " AND guest_name = '" + guestName + "'";
        try (Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery(sql);
            if (resultSet.next()){
                int roomNumber=resultSet.getInt("roomNumber");
                System.out.println(" Room Reservation ID " + reservationId + "For GUEST" +guestName+ "is:" +roomNumber);
            }else {
                System.out.println("Enter valid Details");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }


    }
    private static void updateReservation(Connection connection,Scanner scanner)throws  SQLException{  try {
        System.out.print("Enter reservation ID to update: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        System.out.print("Enter new guest name: ");
        String newGuestName = scanner.nextLine();
        System.out.print("Enter new room number: ");
        int newRoomNumber = scanner.nextInt();
        System.out.print("Enter new contact number: ");
        String newContactNumber = scanner.next();

        String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                "room_number = " + newRoomNumber + ", " +
                "contact_number = '" + newContactNumber + "' " +
                "WHERE reservation_id = " + reservationId;

        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows > 0) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Reservation update failed.");
            }

        }

    }catch (SQLException e){
        e.printStackTrace();
    }

    }
    private static void deleteReservation(Connection connection,Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }


    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}


