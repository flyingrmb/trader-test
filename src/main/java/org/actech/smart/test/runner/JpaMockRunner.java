package org.actech.smart.test.runner;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.actech.smart.common.utils.GenericClassUtils;
import org.actech.smart.common.utils.JacksonUtils;
import org.actech.smart.test.annotation.MockData;
import org.actech.smart.test.annotation.MockDataInit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runners.model.InitializationError;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by paul on 2018/3/18.
 */
public class JpaMockRunner extends SpringJUnit4ClassRunner {
    private static final Log logger = LogFactory.getLog(JpaMockRunner.class);

    public JpaMockRunner(Class<?> clazz) throws InitializationError, IOException {
        super(clazz);

        initMockData(clazz);
    }

    private void initMockData(Class<?> clazz) throws IOException {
        ApplicationContext applicationContext = this.getTestContextManager().getTestContext().getApplicationContext();
        if (applicationContext == null) return ;

        MockDataInit mockDataInit = AnnotationUtils.findAnnotation(clazz, MockDataInit.class);
        if (mockDataInit == null) return ;

        MockData[] mockDatas = mockDataInit.value();
        if (mockDatas == null) return ;

        ObjectMapper mapper = new ObjectMapper();
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        for (MockData mockData : mockDatas) {
            initMockData(mockData, mapper, resourceLoader, applicationContext);
        }
    }

    private void initMockData(MockData mockData, ObjectMapper mapper, ResourceLoader resourceLoader, ApplicationContext applicationContext) throws IOException {
        String file = mockData.file();
        Class<? extends CrudRepository> repositoryClazz = mockData.repository();

        Class entityClazz = GenericClassUtils.getGenericParameterizedType(repositoryClazz, CrudRepository.class, 0);
        if (file == null || file.trim().length() == 0) return ;

        logger.info("load data...,file=" + file + ", repository=" + repositoryClazz + ", entity=" + entityClazz);

        CrudRepository repository = applicationContext.getBean(repositoryClazz);
        JavaType javaType = JacksonUtils.getCollectionType(ArrayList.class, entityClazz);

        Resource resource = resourceLoader.getResource(file);
        repository.saveAll(mapper.readValue(resource.getFile(), javaType));
    }
}
