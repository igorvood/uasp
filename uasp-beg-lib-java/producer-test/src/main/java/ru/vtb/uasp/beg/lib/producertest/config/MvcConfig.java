package ru.vtb.uasp.beg.lib.producertest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("mvc")
class MvcConfig implements WebMvcConfigurer {
}
