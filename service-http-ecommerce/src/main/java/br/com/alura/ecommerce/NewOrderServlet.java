package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
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

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var orderId = req.getParameter("uuid");
            var amount = new BigDecimal(req.getParameter("amount")).setScale(2, RoundingMode.HALF_UP);
            var email = req.getParameter("email");
            var order = new Order(orderId, amount, email);

            try (OrdersDatabase database = new OrdersDatabase()) {
                if (database.saveNew(order)) {
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, new CorrelationId(NewOrderServlet.class.getSimpleName()), order);

                    LOGGER.info("New order sent successfully.");
                    resp.getWriter().println("New order sent");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return;
                }

                LOGGER.info("Old order recevied.");
                resp.getWriter().println("Old order recevied");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (ExecutionException | SQLException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException(e);
        }
    }

    private void saveNewOrderInDatabase(Order order, OrdersDatabase database) {
    }
}
