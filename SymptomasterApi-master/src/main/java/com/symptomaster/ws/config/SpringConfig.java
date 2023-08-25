package com.symptomaster.ws.config;

import com.symptomaster.ws.config.jpa.DataBaseConfig;
import com.symptomaster.ws.interceptors.UserAccessInterceptor;
import com.symptomaster.ws.service.CategoryService;
import com.symptomaster.ws.service.SymptomService;
import com.symptomaster.ws.service.impl.CategoryServiceImpl;
import com.symptomaster.ws.service.impl.SymptomServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.beans.PropertyVetoException;

/**
 * Created by Maxim Kasyanov on 05/02/16.
 *
 * This is configuration class for spring components
 * In this files initialise bean with dependencies
 */
@Configuration
@EnableWebMvc
@ComponentScan({"com.symptomaster.ws"})
public class SpringConfig extends WebMvcConfigurerAdapter {


    @Autowired
    DataBaseConfig dataBaseConfig;

    /*This method need for display static files like css, image, and js files */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /*This method need for set up suffix(jsp files extension) and prefix(path to jsp files) for the view*/
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver internalResourceViewResolver
                = new InternalResourceViewResolver();

        internalResourceViewResolver.setViewClass(JstlView.class);
        internalResourceViewResolver.setPrefix("/WEB-INF/pages/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }

    @Bean
    public SymptomService symptomService (){
        return new SymptomServiceImpl();
    }

    public CategoryService categoryService (){
        return new CategoryServiceImpl();
    }


    @Bean
    public JdbcTemplate jdbcTemplate () throws PropertyVetoException {

        return new JdbcTemplate(dataBaseConfig.dataSource());

    }

    @Bean
    public UserAccessInterceptor userAccessInterceptor() {
        return new UserAccessInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAccessInterceptor()).addPathPatterns(
                "/user/history/**",
                "/user/update/",
                "/user/updatepassword/",
                "/user/profile/**",
                "/symptoms-history/delete/transaction/",
                "/symptoms-history/delete/transaction-entry/"
        );
    }

    /*
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
<property name="dataSource" ref="ds"></property>
</bean>
*/
}
