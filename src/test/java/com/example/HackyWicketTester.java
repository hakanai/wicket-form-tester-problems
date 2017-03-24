package com.example;

import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;

public class HackyWicketTester extends WicketTester {
    public HackyWicketTester(WebApplication application) {
        super(application);

        application.setRequestCycleProvider(new HackyRequestCycleProvider(application.getRequestCycleProvider()));
    }

    private class HackyRequestCycleProvider implements IRequestCycleProvider {
        private final IRequestCycleProvider delegate;

        public HackyRequestCycleProvider(IRequestCycleProvider delegate) {
            this.delegate = delegate;
        }

        public RequestCycle get(RequestCycleContext context) {
            context.setRequestMapper(new HackyRequestMapper(context.getRequestMapper()));
            return this.delegate.get(context);
        }
    }

    private class HackyRequestMapper implements IRequestMapper {
        private final IRequestMapper delegate;

        public HackyRequestMapper(IRequestMapper delegate) {
            this.delegate = delegate;
        }

        public int getCompatibilityScore(Request request) {
            return this.delegate.getCompatibilityScore(request);
        }

        public Url mapHandler(IRequestHandler requestHandler) {
            System.out.println("requestHandler = " + requestHandler);

            if (requestHandler instanceof BookmarkableListenerInterfaceRequestHandler)
            {
                PageParameters hacks = ((BookmarkableListenerInterfaceRequestHandler) requestHandler).getPageParameters();
                PageParameters add = ((BookmarkableListenerInterfaceRequestHandler) requestHandler).getPage().getPageParameters();
                for (PageParameters.NamedPair pair : add.getAllNamed())
                {
                    hacks.add(pair.getKey(), pair.getValue());
                }
            }

            Url result = this.delegate.mapHandler(requestHandler);

            System.out.println("->      result = " + result);
            return result;
        }

        public IRequestHandler mapRequest(Request request) {
            return this.delegate.mapRequest(request);
        }
    }

}
