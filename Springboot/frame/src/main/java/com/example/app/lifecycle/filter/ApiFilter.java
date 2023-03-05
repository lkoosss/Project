package com.example.app.lifecycle.filter;

import com.example.common.value.Constant.LogMarker;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
public class ApiFilter implements Filter {


    /**
     * <pre>
     *  init
     * </pre>
     *
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info(LogMarker.config, "");
        log.info(LogMarker.config, "##### Api Filter Init");
    }

    /**
     * <pre>
     *  doFilter
     * </pre>
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        /** 처리 전 **/
        ////////// 필요 시 구현 //////////

        chain.doFilter(request, response);
        /** 처리 후 **/
        ////////// 필요 시 구현 //////////
    }

    /**
     * <pre>
     *  destroy
     * </pre>
     */
    @Override
    public void destroy() {
        log.info(LogMarker.config, "##### HttpFilter Destroy");
    }
}
