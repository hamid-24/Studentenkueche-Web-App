package de.hsflensburg.certainlyuncertainse2hausarbeit.configuration;

import de.hsflensburg.certainlyuncertainse2hausarbeit.filters.GlobalLoginFilter;
import de.hsflensburg.certainlyuncertainse2hausarbeit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private UserService userService;

    // use the Global login filter for every routes beginning with /recipes or /user
    // for more see GlobalLoginFilter class
    @Bean
    public FilterRegistrationBean<GlobalLoginFilter> GlobalLoginFilter() {
        final var registrationBean = new FilterRegistrationBean<GlobalLoginFilter>();
        registrationBean.setFilter(new GlobalLoginFilter());
        registrationBean.addUrlPatterns("/recipes/*");
        registrationBean.addUrlPatterns("/user/*");
        return registrationBean;
    }
}
