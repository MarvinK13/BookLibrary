package repository;

import configuration.DatabaseConnection;
import model.RentedBooks;
import service.IllegalBookException;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class RentedAtRepository {

    DatabaseConnection databaseConnection = new DatabaseConnection();

    public RentedAtRepository() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading driver class");
        }
    }


    public void addRented(RentedBooks rentedBooks) {
        String sql = "INSERT INTO rentedBooks (bookId,userId,rentedAt,overdrawn,givenBack)VALUES ((SELECT bookId from books Where bookId= ?),(SELECT userId from members Where userId= ?),?,?,null)";
        try (Connection databaseConnection = this.databaseConnection.getConnection();
             PreparedStatement prepareStatement = databaseConnection.prepareStatement(sql);
        ) {
            Calendar calender = Calendar.getInstance();
            Date now = calender.getTime();
            calender.add(Calendar.DAY_OF_YEAR, 14);
            Date calenderTime = calender.getTime();
            Timestamp endDate = new Timestamp(calenderTime.getTime());

            prepareStatement.setInt(1, rentedBooks.getBookId());
            prepareStatement.setInt(2, rentedBooks.getUserId());
            prepareStatement.setTimestamp(3, new Timestamp(now.getTime()));
            prepareStatement.setTimestamp(4, endDate);

            prepareStatement.executeUpdate();
            System.out.println("Finished Task");

        } catch (SQLException exception) {
            System.out.println("Error while connecting to database " + exception);
        }
    }

    public void modifyOverDrawn(int bookId, int userId, String overdrawn) {
        String sql = "UPDATE rentedBooks SET overdrawn= ? WHERE bookId = ? AND userId = ?";

        try (Connection databaseConnection = this.databaseConnection.getConnection();
             PreparedStatement prepareStatement = databaseConnection.prepareStatement(sql);
        ) {
            Calendar calender = Calendar.getInstance();
            calender.add(Calendar.DAY_OF_YEAR, 7);
            Date calenderTime = calender.getTime();
            Timestamp endDate = new Timestamp(calenderTime.getTime());

            prepareStatement.setTimestamp(1, endDate);
            prepareStatement.setInt(2, bookId);
            prepareStatement.setInt(3, userId);

            prepareStatement.executeUpdate();
            System.out.println("Finished Task");

        } catch (SQLException exception) {
            System.out.println("Error while connecting to database " + exception);
        }
    }

    public void giveBackRentedByBookId(RentedBooks rentedBooks) {
        String sql = "UPDATE rentedBooks SET givenBack= ? WHERE bookId = ? AND userId = ?";
        try (Connection databaseConnection = this.databaseConnection.getConnection();
             PreparedStatement prepareStatement = databaseConnection.prepareStatement(sql);
        ) {
            Calendar calender = Calendar.getInstance();
            Date now = calender.getTime();
            prepareStatement.setTimestamp(1, new Timestamp(now.getTime()));
            prepareStatement.setInt(2, rentedBooks.getBookId());
            prepareStatement.setInt(3, rentedBooks.getUserId());
            prepareStatement.executeUpdate();

        } catch (SQLException exception) {
            System.out.println("Error while connecting to database " + exception);
        }

    }

    public List<RentedBooks> findAllRentedBooks() {
        String sql = "select books.title,rentedBooks.bookId,members.name,rentedBooks.userId,rentedAt,overdrawn from rentedBooks Left Join members on members.userId =rentedBooks.userId Left Join books on books.bookId=rentedBooks.bookId Where givenBack is NULL";

        try (Connection databaseConnection = this.databaseConnection.getConnection();
             PreparedStatement prepareStatement = databaseConnection.prepareStatement(sql);
             ResultSet resultSet = prepareStatement.executeQuery();
        ) {
            List<RentedBooks> rentedBooks = new LinkedList<>();
            while (resultSet.next()) {
                RentedBooks rentedBook = new RentedBooks();
                rentedBook.setBookId(resultSet.getInt("bookId"));
                rentedBook.setUserId(resultSet.getInt("userId"));
                rentedBook.setRentedAt(resultSet.getDate("rentedAt"));
                rentedBook.setOverdrawn(resultSet.getString("overdrawn"));
                rentedBook.setName(resultSet.getString("name"));
                rentedBook.setTitle(resultSet.getString("title"));
                rentedBooks.add(rentedBook);
            }
            return rentedBooks;
        } catch (SQLException exception) {
            System.out.println("Error while connecting to database " + exception);
        }

        return null;
    }

    public List<RentedBooks> findAllRentedBooksByUserId(int id) {
        String sql = "SELECT members.name,books.title,rentedAt,overdrawn,rentedBooks.bookId,rentedBooks.userId FROM rentedBooks,members,books Where members.userId=rentedBooks.userId AND members.userId= ? Group By books.title";

        try (Connection databaseConnection = this.databaseConnection.getConnection();
             PreparedStatement prepareStatement = databaseConnection.prepareStatement(sql);
        ) {
            prepareStatement.setInt(1, id);
            ResultSet resultSet = prepareStatement.executeQuery();
            List<RentedBooks> rentedBooks = new LinkedList<>();
            while (resultSet.next()) {
                RentedBooks rentedBook = new RentedBooks();
                rentedBook.setBookId(resultSet.getInt("bookId"));
                rentedBook.setUserId(resultSet.getInt("userId"));
                rentedBook.setRentedAt(resultSet.getDate("rentedAt"));
                rentedBook.setOverdrawn(resultSet.getString("overdrawn"));
                rentedBook.setName(resultSet.getString("name"));
                rentedBook.setTitle(resultSet.getString("title"));
                rentedBooks.add(rentedBook);
            }
            return rentedBooks;
        } catch (SQLException exception) {
            System.out.println("Error while connecting to database at findAllRentedBooksByUserId " + exception);
        }

        return null;
    }

    public List<RentedBooks> findAllRentedBooksByBookId(int id) {
        String sql = "SELECT members.name,books.title,rentedAt,overdrawn,rentedBooks.bookId,rentedBooks.userId FROM rentedBooks,members,books Where members.userId=rentedBooks.userId AND books.bookId= ? Group By books.title";

        try (Connection databaseConnection = this.databaseConnection.getConnection();
             PreparedStatement prepareStatement = databaseConnection.prepareStatement(sql);
        ) {
            prepareStatement.setInt(1, id);
            ResultSet resultSet = prepareStatement.executeQuery();
            List<RentedBooks> rentedBooks = new LinkedList<>();
            while (resultSet.next()) {
                RentedBooks rentedBook = new RentedBooks();
                rentedBook.setBookId(resultSet.getInt("bookId"));
                rentedBook.setUserId(resultSet.getInt("userId"));
                rentedBook.setRentedAt(resultSet.getDate("rentedAt"));
                rentedBook.setOverdrawn(resultSet.getString("overdrawn"));
                rentedBook.setName(resultSet.getString("name"));
                rentedBook.setTitle(resultSet.getString("title"));
                rentedBooks.add(rentedBook);
            }
            return rentedBooks;
        } catch (SQLException exception) {
            System.out.println("Error while connecting to database " + exception);
        }

        return null;
    }

    public int findBookIdbyUser(int bookId, int userId) {
        String sql = "SELECT Count(*),givenBack,rentedAt FROM rentedBooks WHERE userId = ? AND bookId = ? AND rentedAt= (Select max(rentedAt) From rentedBooks);";

        try (Connection databaseConnection = this.databaseConnection.getConnection();
             PreparedStatement prepareStatement = databaseConnection.prepareStatement(sql);
        ) {
            Calendar calender = Calendar.getInstance();
            Date now = calender.getTime();
            Timestamp timestamp = new Timestamp(now.getTime());

            prepareStatement.setInt(1, userId);
            prepareStatement.setInt(2, bookId);
            ResultSet resultSet = prepareStatement.executeQuery();

            resultSet.next();
            int id = resultSet.getInt("Count(*)");
            Timestamp date = resultSet.getTimestamp("givenBack");
            Timestamp rentedAt = resultSet.getTimestamp("rentedAt");
            resultSet.close();

            //formats date so it can be
            if (rentedAt == null) {
                return 0;
            }


            /*
            Date fisrtDate = new Date(timestamp.getTime());
                Date secondDate = new Date(rentedAt.getTime());
                DateFormat df = new SimpleDateFormat("YYYY-MM-dd");
                String firstDateStr = df.format(fisrtDate);
                String secondDateStr = df.format(secondDate);
                if (firstDateStr.equals(secondDateStr)) {
                    return 1;
                }
            if (id == 0) {
                return 0;
            } else if (id != 0 && date != null) {
                return 0;
            } else {
                return 1;
            }
             */

        } catch (SQLException exception) {
            System.out.println("Error while connecting to database " + exception);

        }
        return 1;
    }


}
