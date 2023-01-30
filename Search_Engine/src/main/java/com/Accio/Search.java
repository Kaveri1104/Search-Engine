package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request,HttpServletResponse response){
        //get parameter called "keyword" from request
        String keyword = request.getParameter("keyword");
        System.out.println(keyword);
        try {
                //Establish a connection to the database
                Connection connection = DatabaseConnection.getConnection();
                //Save the keyword and link associated into history table
                PreparedStatement preparedStatement = connection.prepareStatement("Insert into history values(?,?)");
                preparedStatement.setString(1,keyword);
                preparedStatement.setString(2,"http://localhost:8080/Search_/Search?keyword="+keyword);
                preparedStatement.executeUpdate();
                //Execute a query related to keyword and get the result
                ResultSet resultSet = connection.createStatement().executeQuery("select pagetitle,pagelink,(length(lower(pagetext))-length(replace(lower(pagetext),'" + keyword + "','')))/length('" + keyword + "') as countoccurences\n" +
                        "from pages\n" +
                        "order by countoccurences desc\n" +
                        "limit 30;");
                ArrayList<SearchResult> results = new ArrayList<SearchResult>();
                //iterate through resultSet and save all elements in the results list
                while (resultSet.next()) {
                    SearchResult searchResult = new SearchResult();
                    //setting page title
                    searchResult.setPageTitle(resultSet.getString("pageTitle"));
                    //setting page link
                    searchResult.setPageLink(resultSet.getString("pageLink"));
                    //adding to the result list
                    results.add(searchResult);
                }
                //Display results in console
                for(SearchResult searchResult:results){
                    System.out.println(searchResult.getPageTitle()+" "+searchResult.getPageLink());
                }
                //set the attribute of the request with results arraylist
                request.setAttribute("results",results);
                //forward request to the front end or search.jsp
                request.getRequestDispatcher("/search.jsp").forward(request,response);
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
        }
        catch(SQLException | ServletException | IOException sqlException){
            sqlException.printStackTrace();
        }
    }
}
