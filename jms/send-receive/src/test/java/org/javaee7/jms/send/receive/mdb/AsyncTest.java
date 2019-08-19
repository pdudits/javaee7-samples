package org.javaee7.jms.send.receive.mdb;

import org.javaee7.jms.send.receive.simple.MessageSenderAsync;
import org.junit.Test;

import java.io.File;

import javax.ejb.EJB;

import org.javaee7.jms.send.receive.Resources;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Patrik Dudits
 */
@RunWith(Arquillian.class)
public class AsyncTest {

    @EJB
    MessageSenderAsync asyncSender;

    @EJB
    ReceiverLogger logger;
    
    private final int messageReceiveTimeoutInMillis = 10000;

    @Test
    public void testAsync() throws InterruptedException {
        asyncSender.sendMessage("Fire!");
        ReceptionSynchronizer.waitFor(MessageReceiverAsync.class, "onMessage" , messageReceiveTimeoutInMillis);
        // unless we timed out, the test passes
        assertEquals("AroundConstruct should only be intercepted once", 1, logger.getConstructed());
        assertEquals("AroundInvoke should only be intercepted once", 1, logger.getReceived());
    }

    @Deployment
    public static WebArchive deploy() {
        return ShrinkWrap.create(WebArchive.class)
            .addClass(MessageSenderAsync.class)
            .addClass(Resources.class)
            .addClass(MessageReceiverAsync.class)
            .addClass(ReceptionSynchronizer.class)
            .addClass(ReceiverInterceptor.class)
            .addClass(ReceiverLogger.class)
            .addAsWebInfResource(new File("src/test/resources/WEB-INF/ejb-jar.xml"));
    }

}
