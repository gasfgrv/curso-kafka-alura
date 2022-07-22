package br.com.alura.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewOrderServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewOrderServlet.class.getName());
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String orderId = UUID.randomUUID().toString();
            BigDecimal amount = new BigDecimal(req.getParameter("amount"))
                    .setScale(2, RoundingMode.HALF_UP);
            String email = req.getParameter("email");

            Order order = new Order(orderId, amount, email);
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, new CorrelationId(NewOrderServlet.class.getSimpleName()), order);

            String emailCode = "Thank you for your order! We are processing your order!";
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, new CorrelationId(NewOrderServlet.class.getSimpleName()),emailCode);

            LOGGER.info("New order sent successfully.");
            resp.getWriter().println("New order sent");
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException(e);
        }
    }
}
