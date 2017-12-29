
import java.util.Locale;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import pl.jrj.mdb.IMdbManager;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@MessageDriven(mappedName = "jms/MyQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge")
    ,
    @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "javax.jms.Queue")
})
public class MdbBean implements MessageListener {

    static boolean isStarted = false;
    static String sessionId = null;
    static Integer errors = 0;
    static Double counter = 0.0;

    /**
     * Queue processing
     * @param message message info
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage == false) 
                return;

            String msg = ((TextMessage) message).getText();
            if (msg.equals("start")) 
                handleStart();
            else if (msg.equals("stop")) 
                handleStop();
            else if (msg.equals("val")) 
                respond(String.format(Locale.ENGLISH,
                        sessionId + "/%.0f", counter));
            else if (msg.equals("err")) 
                respond(sessionId + "/" + errors.toString());
            else if (msg.equals("inc")) 
                handleInc();
            else if (msg.equals("dec")) 
                handleDec();
            else if (msg.contains("inc/")) 
                handleSplittedInc(msg);
            else if (msg.contains("dec/")) 
                handleSplittedDec(msg);
            else 
                errors++;

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    private void handleStart() {
        if (isStarted) {
            errors++;
        } else {
            sessionId = register();
            isStarted = true;
        }
    }

    private void handleStop() {
        if (isStarted) {
            isStarted = false;
        } else {
            errors++;
        }
    }

    private void handleInc() {
        if (isStarted) {
            counter++;
        } else {
            errors++;
        }
    }

    private void handleDec() {
        if (isStarted) {
            counter--;
        } else {
            errors++;
        }
    }

    private void handleSplittedInc(String msg) {
        Double val = getSplitted(msg);
        if (val != null) {
            counter += val;
        }
    }

    private void handleSplittedDec(String msg) {
        Double val = getSplitted(msg);
        if (val != null) {
            counter -= val;
        }
    }

    private Double getSplitted(String msg) {
        if (!isStarted) {
            errors++;
            return null;
        }

        String[] arr = msg.split("/");
        if (arr[1] == null) {
            errors++;
            return null;
        } else {
            return parseVal(arr[1]);
        }
    }

    private Double parseVal(String val) {
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            errors++;
            return null;
        }
    }

    private void respond(String msg)
            throws JMSException, NamingException,
            JMSException, NullPointerException {

        InitialContext context = new InitialContext();
        TopicConnectionFactory factory = (TopicConnectionFactory) 
                context.lookup("jms/ConnectionFactory");
        sendTopic(factory, context, msg);
    }

    private void sendTopic(TopicConnectionFactory factory,
            InitialContext context, String msg)
            throws JMSException, NamingException {

        try (TopicConnection con = factory.createTopicConnection()) {
            con.start();
            TopicSession ses = con.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic t = (Topic) context.lookup("jms/MyTopic");
            TopicPublisher pub = ses.createPublisher(t);
            TextMessage message = ses.createTextMessage();
            message.setText(msg);
            pub.publish(message);
        }
    }

    private String register() {
        return "asd";
        // try {
        //     pl.jrj.mdb.IMdbManager man = (IMdbManager) new InitialContext()
        //             .lookup("java:global/mdb-project"
        //                     + "/MdbManager!pl.jrj.mdb.IMdbManager");
        //     return man.sessionId("124624");
        // } catch (NamingException e) {
        //     e.printStackTrace();
        // }

        // return null;
    }
}
