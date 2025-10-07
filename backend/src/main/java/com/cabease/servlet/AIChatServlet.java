package com.cabease.servlet;

import com.cabease.util.GeminiAPIHelper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ai-chat")
public class AIChatServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        String message = request.getParameter("message");
        PrintWriter out = response.getWriter();

        if (message != null && !message.trim().isEmpty()) {
            String aiResponse = GeminiAPIHelper.getAIResponse(message);
            out.print(aiResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("Error: Message parameter is missing or empty.");
        }
        out.flush();
    }
}
