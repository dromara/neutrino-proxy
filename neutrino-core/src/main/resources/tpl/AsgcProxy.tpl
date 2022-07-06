package ${package};

#foreach($item in ${importList})
import ${item.getName()};
#end

#if(${targetIsInterface})
public class ${proxyClassName} implements ${targetType.getSimpleName()} {
#else
public class ${proxyClassName} extends ${targetType.getSimpleName()} {
#end

#foreach($methodInfo in ${methodInfoList})
    public ${methodInfo.returnType.getSimpleName()} ${methodInfo.methodName} (${methodInfo.parametersString})${methodInfo.throwsString}{
        Invocation inv = new Invocation(${methodInfo.methodId}L, this, () -> {
#if(!${targetIsInterface})
#if(${methodInfo.void})
            super.${methodInfo.methodName}(${methodInfo.parameterNamesString});
            return null;
#else
            return super.${methodInfo.methodName}(${methodInfo.parameterNamesString});
#end
#else
            return null;
#end
        });
        try {
            inv.invoke();
        } catch(RuntimeException e) {
            throw e;
        } catch(Exception e) {
#if(${methodInfo.throw})
            if (ProxyCache.checkMethodThrow(${methodInfo.methodId}L, e)) {
                throw e;
            } else {
                e.printStackTrace();
            }
#else
             e.printStackTrace();
#end
        }
#if(!${methodInfo.void})
        return inv.getReturnValue();
#end
    }

#end
}
