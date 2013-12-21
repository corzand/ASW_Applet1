/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import asw1009.HTTPClient;
import asw1009.ManageXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 *
 * @author Andrea
 */
public class ChatApplet extends JApplet {

    
    //final String BASE = "http://localhost:8080/WebApplicationServices/";
    JLabel l = new JLabel("not logged");
    JTextField t = new JTextField(10);
    int i = 0;
    JLabel lView = new JLabel("not started");
    JTextField tView = new JTextField(10);
    int iView = 0;
    Document answerView;
    String msgView = "";
    HTTPClient hc = new HTTPClient();
    boolean logged = false;
    JLabel lUser = new JLabel("user: unknown");
    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser.
     */
    public void init() {
        try {
            //hc.setBase(new URL(BASE));
        
            hc.setSessionId(getParameter("sessionId"));
            hc.setBase(getDocumentBase());
            
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
    //                JFrame frame = new JFrame("sender");
    //                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //                frame.setVisible(true);
    //                frame.setVisible(true);

                    Container cp = getContentPane();
                    cp.setLayout(new GridLayout(3,2));
    
                    cp.add(t);
                    cp.add(l);
    
                    cp.add(tView);
                    cp.add(lView);
    
                    cp.add(lUser);

                    t.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            try {
                                ManageXML mngXML = new ManageXML();

                                Document data = mngXML.newDocument();
                                Element root = data.createElement(logged ? "push" : "login");
                                root.appendChild(data.createTextNode(t.getText()));
                                data.appendChild(root);

                                Document answer = hc.execute("chat", data);

                                l.setText(answer.getDocumentElement().getTagName() + (logged ? (++i) : ""));
                                if (!logged) {
                                    logged = true;
                                    new Viewer().start();
                                    lView.setText("started");
                                    lUser.setText("user: " + t.getText());
                                }
                                t.setText("");
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        }
                    });
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(ChatApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    class Viewer extends Thread {

        public void run() {
            try {
                ManageXML mngXML = new ManageXML();

                while (!msgView.equals("bye")) {
                    Document data = mngXML.newDocument();
                    Element root = data.createElement("pop");
                    data.appendChild(root);

                    answerView = hc.execute("chat", data);

                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            if (answerView.getDocumentElement().getTagName().equals("push")) {
                                tView.setText((msgView = ((Text) answerView.getDocumentElement().getChildNodes().item(0)).getData()));
                            } else {
                                lView.setText(answerView.getDocumentElement().getTagName() + (++iView));
                            }
                        }
                    });
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        l.setText("finished");
                    }
                });
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
    // TODO overwrite start(), stop() and destroy() methods
}
