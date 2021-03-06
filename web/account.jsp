<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: Ben
  Date: 21/10/2020
  Time: 16:05
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Account</title>
    <!--Here generateNumbers is called when the lucky dip button is pressed, this loops 6 times adding the return
    value of getSecureRandInt to a field in the form until the form is full. In getSecureRandInt a randomly secure
    integer is created, if it is less than 0 or greater than 60, the function calls itself recursively until
    the number is between 0 and 60.-->
    <script type="text/javascript">
        function generateNumbers(){
            for (var i=0; i<6; i++) {
                document.forms["addUN"][i].value = getSecureRandInt();
            }
        }
        function getSecureRandInt (){
            var numbers = new Uint8Array(6);
            window.crypto.getRandomValues(numbers);
            var range = 61;
            var max_range = 256;
            if (numbers[0] >= Math.floor(max_range/range)*range) {
                return getSecureRandInt();
            }
            return (numbers[0] % range);
        }
        function validateForm(){
            for (var i=0; i<6; i++) if (document.forms["addUN"][i].value === "") {
                alert("Number/s blank.");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<h1>User Account</h1>
    <!--If the current session role is not valid the user is returned to the home page-->
    <%if(session.getAttribute("role").equals("admin") || session.getAttribute("role") == null){
        response.sendRedirect("index.jsp");
    }%>
<p><%= request.getAttribute("message") %></p>
<h1>User Details</h1>
<div>
    <output>Firstname: </output><%= session.getAttribute("firstname") %><br>
    <output>Lastname: </output><%= session.getAttribute("lastname") %><br>
    <output>Username: </output><%= session.getAttribute("username") %><br>
    <output>Email: </output><%= session.getAttribute("email") %><br>
</div>
<h2>Select New Lottery Draw</h2>
<div>
    <form name="addUN" method="post" onsubmit="return validateForm()" action="AddUserNumbers">
        <label for="nu1">No.1:</label>
        <input type="number" id="nu1" name="nu1" min="0" max="60"><br>
        <label for="nu2">No.2:</label>
        <input type="number" id="nu2" name="nu2" min="0" max="60"><br>
        <label for="nu3">No.3:</label>
        <input type="number" id="nu3" name="nu3" min="0" max="60"><br>
        <label for="nu4">No.4:</label>
        <input type="number" id="nu4" name="nu4" min="0" max="60"><br>
        <label for="nu5">No.5:</label>
        <input type="number" id="nu5" name="nu5" min="0" max="60"><br>
        <label for="nu6">No.6:</label>
        <input type="number" id="nu6" name="nu6" min="0" max="60"><br>
        <button type="submit" value="Submit">Submit</button>
    </form>
    <button onclick="generateNumbers()">Lucky Dip</button><br>
    <form name="getDraws" method="post" action="GetUserNumbers">
        <button type="submit" value="Submit">Get Draws</button>
    </form>
</div>
<div>
    <!--This gets the array of the draws from the request attribute and loops through the array and displays the draw/s
     on the page with spaces in between, if there are draws to be displayed-->
    <p>
        <%
        if(request.getAttribute("drawsTitle")!=null){
        %>
        <%=request.getAttribute("drawsTitle")%><br>
        <%ArrayList<String> data = (ArrayList<String>) request.getAttribute("draws");
        for (int i=0; i<data.size(); i++){
            %><%=data.get(i)%><%;%>
            <br><%
            }%>
        <form name="getWDraw" method="post" action="CheckDraws">
            <button type="submit" value="Submit">Check Draws</button>
        </form><%
        }%>
    </p>
    <br>
</div>
<div>
    <%if (request.getAttribute("winMessage")!= null){%>
    <%=request.getAttribute("winMessage")%><%}%>
</div>
<a href="index.jsp">Home Page</a>
</body>
</html>