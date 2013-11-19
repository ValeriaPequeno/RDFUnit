package org.aksw.databugger.ui;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.aksw.databugger.Databugger;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.aksw.databugger.tripleReaders.TripleReader;
import org.aksw.databugger.tripleReaders.TripleReaderFactory;

import java.io.File;

/**
 * User: Dimitris Kontokostas
 * Keeps user session variables
 * Created: 11/15/13 9:52 AM
 */
public class DatabuggerUISession extends VaadinSession {

    protected static  Databugger databugger = null;
    protected static  String baseDir = null;


    public DatabuggerUISession(VaadinService service) {
        super(service);
    }

    public static Databugger getDatabugger(){

        if (databugger == null) {

            try {
                File f = VaadinSession.getCurrent().getService().getBaseDirectory();
                baseDir = f.getAbsolutePath()+"/data/";

                DatabuggerUtils.fillPrefixService(baseDir + "prefixes.ttl");

                TripleReader patternReader = TripleReaderFactory.createTripleFileReader(baseDir + "patterns.ttl");
                TripleReader testGeneratorReader = TripleReaderFactory.createTripleFileReader(baseDir+"testGenerators.ttl");
                databugger = new Databugger(patternReader, testGeneratorReader);
            } catch (Exception e) {
                //TODO
            }

        }
        return databugger;

    }
}