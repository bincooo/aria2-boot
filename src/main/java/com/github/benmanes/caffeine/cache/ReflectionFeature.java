//package com.github.benmanes.caffeine.cache;
//
//import com.oracle.svm.core.annotate.AutomaticFeature;
//import org.graalvm.nativeimage.hosted.Feature;
//import org.graalvm.nativeimage.hosted.RuntimeReflection;
//
//@AutomaticFeature
//public class ReflectionFeature implements Feature {
//    @Override
//    public void beforeAnalysis(BeforeAnalysisAccess access) {
//        try {
//            RuntimeReflection.register(SSSMSW.class);
//            RuntimeReflection.register(SSSMSW.class
//                    .getDeclaredConstructor(Caffeine.class, AsyncCacheLoader.class, boolean.class));
//
//            RuntimeReflection.register(PSWMS.class);
//            RuntimeReflection.register(PSWMS.class.getDeclaredConstructor());
//
////            RuntimeReflection.register(com.github.benmanes.caffeine.cache.SSSMSW.class);
////            RuntimeReflection.register(SSSMSW.class
////                    .getDeclaredConstructor(Caffeine.class, AsyncCacheLoader.class, boolean.class));
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
