package org.pocketcampus.platform.server.launcher;

import org.apache.thrift.TProcessor;
import org.joda.time.LocalDateTime;
import org.pocketcampus.platform.server.RawPlugin;
import org.pocketcampus.platform.server.StateChecker;
import org.pocketcampus.platform.shared.PCConfig;
import org.pocketcampus.platform.shared.PCConstants;

import javax.servlet.http.HttpServlet;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.NoSuchObjectException;
import java.util.*;

public class PocketCampusServer extends ServerBase {
    public static final PCConfig CONFIG = new PCConfig();

    private static final Map<String, Object> plugins = new HashMap<>();

    /**
     * Basic Messenger-like functionality.
     */
    public static Object invokeOnPlugin(String pluginName, String methodName, Object arg)
            throws NoSuchObjectException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        if (!plugins.containsKey(pluginName.toLowerCase())) {
            throw new NoSuchObjectException("Plugin not found: " + pluginName);
        }

        Object plugin = plugins.get(pluginName.toLowerCase());
        return plugin.getClass().getMethod(methodName, arg.getClass()).invoke(plugin, arg);
    }

    /**
     * Gets the request headers of the current request.
     */
    public static Map<String, String> getRequestHeaders() {
        return TrackingThriftServlet.receivedRequestHeaders.get();
    }


    /**
     * @return the ISO language code of the user who generated the request if it is part of the
     * PocketCampus supported languages, or the PocketCampus default language code if not.
     */
    public static String getUserLanguageCode() {
        return getUserLanguageCode(PCConstants.PC_ACCEPTED_LANGUAGES, PCConstants.PC_DEFAULT_LANGUAGE);
    }

    private static String getUserLanguageCode(HashSet<String> acceptedCodes, String defaultCode) {
        if (acceptedCodes == null) {
            throw new IllegalArgumentException("acceptedCodes cannot be null");
        }
        if (defaultCode == null) {
            throw new IllegalArgumentException("defaultCode cannot be null");
        }
        Map<String, String> headers = getRequestHeaders();
        if (headers == null) {
            return defaultCode;
        }
        String langCode = headers.get(PCConstants.HTTP_HEADER_USER_LANG_CODE);
        if (langCode == null) {
            return defaultCode;
        }
        if (acceptedCodes.contains(langCode)) {
            return langCode;
        }
        return defaultCode;
    }

    /**
     * Gets the available services.
     */
    @Override
    protected List<ServiceInfo> getServices() {
        final List<ServiceInfo> processors = new ArrayList<>();

        for (final String pluginName : CONFIG.getString("ENABLED_PLUGINS").split(",")) {
            final Object pluginService = getPluginService(pluginName);
            if (pluginService == null) {
                System.out.println(pluginName + " plugin not found, skipping...");
                continue;
            }

            final TProcessor thriftProcessor = getThriftProcessor(pluginService, pluginName);
            if (thriftProcessor == null) {
                System.out.println(pluginName + " Thrift processor not found, skipping...");
                continue;
            }

            final HttpServlet rawProcessor = getRawProcessor(pluginService);
            final StateChecker stateChecker = getStateChecker(pluginService);

            processors.add(new ServiceInfo(pluginName.toLowerCase(), thriftProcessor, rawProcessor, stateChecker));
            plugins.put(pluginName.toLowerCase(), pluginService);
            System.out.println(pluginName + " plugin started.");
        }
        return processors;
    }

    /**
     * Gets an implementation of the service for the plugin with the specified name.
     */
    private Object getPluginService(final String pluginName) {
        final String serviceName = "org.pocketcampus.plugin." + pluginName.toLowerCase() + ".server." + pluginName + "ServiceImpl";

        final Class<?> serviceClass;
        try {
            serviceClass = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            return null;
        }

        try {
            return serviceClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching the " + pluginName + " plugin.", e);
        }
    }

    /**
     * Gets a Thrift processor for the plugin with the specified service and name.
     */
    private TProcessor getThriftProcessor(final Object pluginService, final String pluginName) {
        if (pluginService == null) {
            return null;
        }

        final String serviceName = "org.pocketcampus.plugin." + pluginName.toLowerCase() + ".server." + pluginName + "ServiceImpl";
        final String sharedPrefix = "org.pocketcampus.plugin." + pluginName.toLowerCase() + ".shared." + pluginName;
        final String interfaceName = sharedPrefix + "Service$Iface";
        final String serviceProcessorName = sharedPrefix + "Service$Processor";

        Class<?> serviceClass;
        Class<?> interfaceClass;
        Class<?> processorClass;
        try {
            serviceClass = Class.forName(serviceName);
            interfaceClass = Class.forName(interfaceName);
            processorClass = Class.forName(serviceProcessorName);
        } catch (ClassNotFoundException e) {
            return null;
        }

        final Class<?> finalServiceClass = serviceClass; // needs to be final for capture
        final Object loggedPluginService = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                final StringBuilder builder = new StringBuilder();
                builder.append("[" + LocalDateTime.now().toString() + "]\t");
                builder.append("plugin=" + pluginName + "\t");
                builder.append("method=" + method.getName() + "\t");
                Deprecated d = finalServiceClass.getMethod(method.getName(), method.getParameterTypes()).getAnnotation(Deprecated.class); 

                long t1 = System.currentTimeMillis();
                try {
                    return method.invoke(pluginService, args);
				} finally {
	                long t2 = System.currentTimeMillis();
	                builder.append("time=" + (t2 - t1) + "ms\t");
	                builder.append("deprecated=" + (d != null) + "\t");
	                System.out.println(builder.toString());
				}
            }
        });

        try {
            return (TProcessor) processorClass
                    .getConstructor(interfaceClass)
                    .newInstance(loggedPluginService);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating the Thrift processor for the " + pluginName + " plugin.", e);
        }
    }

    /**
     * Gets a raw processor, if any, for the plugin with the specified service.
     */
    private HttpServlet getRawProcessor(final Object pluginService) {
        return pluginService instanceof RawPlugin ? ((RawPlugin) pluginService).getServlet() : null;
    }

    /**
     * Gets a state checker, if any, for the plugin with the specified service.
     */
    private StateChecker getStateChecker(final Object pluginService) {
        return pluginService instanceof StateChecker ? ((StateChecker) pluginService) : null;
    }
}