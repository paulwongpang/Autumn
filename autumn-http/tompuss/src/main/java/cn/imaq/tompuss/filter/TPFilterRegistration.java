package cn.imaq.tompuss.filter;

import cn.imaq.tompuss.core.TPRegistration;
import cn.imaq.tompuss.servlet.TPServletContext;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TPFilterRegistration extends TPRegistration<Filter> implements FilterRegistration.Dynamic {
    private Set<String> servletMappings = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Set<String> urlPatternMappings = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public TPFilterRegistration(TPServletContext context, String name, Filter instance) {
        super(context, name, instance);
    }

    public void loadAnnotation(WebFilter wf) {
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.copyOf(Arrays.asList(wf.dispatcherTypes()));
        this.addMappingForUrlPatterns(dispatcherTypes, true, wf.value());
        this.addMappingForUrlPatterns(dispatcherTypes, true, wf.urlPatterns());
        this.addMappingForServletNames(dispatcherTypes, true, wf.servletNames());
        this.setAsyncSupported(wf.asyncSupported());
        for (WebInitParam initParam : wf.initParams()) {
            this.setInitParameter(initParam.name(), initParam.value());
        }
    }

    /**
     * Adds a filter mapping with the given servlet names and dispatcher
     * types for the Filter represented by this FilterRegistration.
     * <p>
     * <p>Filter mappings are matched in the order in which they were
     * added.
     * <p>
     * <p>Depending on the value of the <tt>isMatchAfter</tt> parameter, the
     * given filter mapping will be considered after or before any
     * <i>declared</i> filter mappings of the ServletContext from which this
     * FilterRegistration was obtained.
     * <p>
     * <p>If this method is called multiple times, each successive call
     * adds to the effects of the former.
     *
     * @param dispatcherTypes the dispatcher types of the filter mapping,
     *                        or null if the default <tt>DispatcherType.REQUEST</tt> is to be used
     * @param isMatchAfter    true if the given filter mapping should be matched
     *                        after any declared filter mappings, and false if it is supposed to
     *                        be matched before any declared filter mappings of the ServletContext
     *                        from which this FilterRegistration was obtained
     * @param servletNames    the servlet names of the filter mapping
     * @throws IllegalArgumentException if <tt>servletNames</tt> is null or
     *                                  empty
     * @throws IllegalStateException    if the ServletContext from which this
     *                                  FilterRegistration was obtained has already been initialized
     */
    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        this.context.addFilterMapping(new TPFilterMapping.ByServlet(this, dispatcherTypes, servletNames), isMatchAfter);
        this.servletMappings.addAll(Arrays.asList(servletNames));
    }

    /**
     * Gets the currently available servlet name mappings
     * of the Filter represented by this <code>FilterRegistration</code>.
     * <p>
     * <p>If permitted, any changes to the returned <code>Collection</code> must not
     * affect this <code>FilterRegistration</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the currently
     * available servlet name mappings of the Filter represented by this
     * <code>FilterRegistration</code>
     */
    @Override
    public Collection<String> getServletNameMappings() {
        return Collections.unmodifiableCollection(this.servletMappings);
    }

    /**
     * Adds a filter mapping with the given url patterns and dispatcher
     * types for the Filter represented by this FilterRegistration.
     * <p>
     * <p>Filter mappings are matched in the order in which they were
     * added.
     * <p>
     * <p>Depending on the value of the <tt>isMatchAfter</tt> parameter, the
     * given filter mapping will be considered after or before any
     * <i>declared</i> filter mappings of the ServletContext from which
     * this FilterRegistration was obtained.
     * <p>
     * <p>If this method is called multiple times, each successive call
     * adds to the effects of the former.
     *
     * @param dispatcherTypes the dispatcher types of the filter mapping,
     *                        or null if the default <tt>DispatcherType.REQUEST</tt> is to be used
     * @param isMatchAfter    true if the given filter mapping should be matched
     *                        after any declared filter mappings, and false if it is supposed to
     *                        be matched before any declared filter mappings of the ServletContext
     *                        from which this FilterRegistration was obtained
     * @param urlPatterns     the url patterns of the filter mapping
     * @throws IllegalArgumentException if <tt>urlPatterns</tt> is null or
     *                                  empty
     * @throws IllegalStateException    if the ServletContext from which this
     *                                  FilterRegistration was obtained has already been initialized
     */
    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        this.context.addFilterMapping(new TPFilterMapping.ByUrlPattern(this, dispatcherTypes, urlPatterns), isMatchAfter);
        this.urlPatternMappings.addAll(Arrays.asList(urlPatterns));
    }

    /**
     * Gets the currently available URL pattern mappings of the Filter
     * represented by this <code>FilterRegistration</code>.
     * <p>
     * <p>If permitted, any changes to the returned <code>Collection</code> must not
     * affect this <code>FilterRegistration</code>.
     *
     * @return a (possibly empty) <code>Collection</code> of the currently
     * available URL pattern mappings of the Filter represented by this
     * <code>FilterRegistration</code>
     */
    @Override
    public Collection<String> getUrlPatternMappings() {
        return Collections.unmodifiableCollection(this.urlPatternMappings);
    }
}
