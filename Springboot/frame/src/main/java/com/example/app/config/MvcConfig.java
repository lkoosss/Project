package com.example.app.config;

import com.example.app.lifecycle.filter.ApiFilter;
import com.example.app.lifecycle.interceptor.ApiInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    /**
     * <pre>
     *  registerApiFilter
     *  - ApiFilter 등록
     * </pre>
     *
     * @return
     */
    public FilterRegistrationBean<ApiFilter> registerApiFilter() {
        boolean enable = true;
        int order = 1;
        List<String> urlPattern = Arrays.asList("/*");
        return this.createFilterRegistration(new ApiFilter(), enable, order, urlPattern);
    }

    /**
     * <pre>
     *  configureViewResolvers
     *  - ViewResolver 설정
     * </pre>
     *
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views", ".jsp");
    }

    /**
     * <pre>
     *  addResourceHandlers
     *  - JS, CSS 파일의 Search Path 지정부분
     * </pre>
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(20);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(20);
    }

    /**
     * <pre>
     *  addInterceptors
     *  - 인터셉터 추가설정 부분
     * </pre>
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiInterceptor())       // Interceptor추가하고
                .addPathPatterns("/**")                         // 어느 URI일 때 인터셉터에 넘겨줄지
                .excludePathPatterns("");                       // 어느 URI는 제외시킬 것인지
    }

    /**
     * <pre>
     *  createFilterRegistration
     *  - 필터 등록
     * </pre>
     *
     * @param filter
     * @param enable
     * @param order
     * @param urlPattern
     * @return
     */
    private FilterRegistrationBean createFilterRegistration(Filter filter, boolean enable, int order, List<String> urlPattern) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(filter);
        filterRegistrationBean.setEnabled(enable);
        filterRegistrationBean.setOrder(order);
        filterRegistrationBean.setUrlPatterns(urlPattern);
        filterRegistrationBean.setAsyncSupported(true);
        return filterRegistrationBean;
    }
}
