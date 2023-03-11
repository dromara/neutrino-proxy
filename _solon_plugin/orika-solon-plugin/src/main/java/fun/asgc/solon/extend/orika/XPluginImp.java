package fun.asgc.solon.extend.orika;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author: aoshiguchen
 * @date: 2023/3/11
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        DefaultMapperFactory factory = new DefaultMapperFactory.Builder().build();

        context.subWrapsOfType(CustomConverter.class, bw -> {
            factory.getConverterFactory().registerConverter(bw.raw());
        });
        context.subWrapsOfType(Mapper.class, bw -> {
            factory.registerMapper(bw.raw());
        });
        context.subWrapsOfType(ClassMapBuilder.class, bw -> {
            factory.registerClassMap((ClassMapBuilder<? extends Object, ? extends Object>) bw.raw());
        });
        context.wrapAndPut(MapperFactory.class, factory);
        context.beanScan("fun.asgc.solon.extend.orika");
    }

}
