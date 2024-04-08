package com.whx.router.compiler.doc;

import static com.whx.router.compiler.utils.Constants.BOOLEAN_PACKAGE;
import static com.whx.router.compiler.utils.Constants.BOOLEAN_PRIMITIVE;
import static com.whx.router.compiler.utils.Constants.BYTE_PACKAGE;
import static com.whx.router.compiler.utils.Constants.BYTE_PRIMITIVE;
import static com.whx.router.compiler.utils.Constants.CHAR_PACKAGE;
import static com.whx.router.compiler.utils.Constants.CHAR_PRIMITIVE;
import static com.whx.router.compiler.utils.Constants.DOCS;
import static com.whx.router.compiler.utils.Constants.DOCUMENT_FILE_NAME;
import static com.whx.router.compiler.utils.Constants.DOUBLE_PACKAGE;
import static com.whx.router.compiler.utils.Constants.DOUBLE_PRIMITIVE;
import static com.whx.router.compiler.utils.Constants.FLOAT_PACKAGE;
import static com.whx.router.compiler.utils.Constants.FLOAT_PRIMITIVE;
import static com.whx.router.compiler.utils.Constants.INTEGER_PACKAGE;
import static com.whx.router.compiler.utils.Constants.INTEGER_PRIMITIVE;
import static com.whx.router.compiler.utils.Constants.LONG_PACKAGE;
import static com.whx.router.compiler.utils.Constants.LONG_PRIMITIVE;
import static com.whx.router.compiler.utils.Constants.SHORT_PACKAGE;
import static com.whx.router.compiler.utils.Constants.SHORT_PRIMITIVE;
import static com.whx.router.compiler.utils.Constants.STRING_PACKAGE;

import com.google.gson.GsonBuilder;
import com.whx.router.annotation.Service;
import com.whx.router.annotation.Interceptor;
import com.whx.router.annotation.Param;
import com.whx.router.annotation.Route;
import com.whx.router.annotation.Service;
import com.whx.router.compiler.doc.model.DocumentModel;
import com.whx.router.compiler.doc.model.InterceptorModel;
import com.whx.router.compiler.doc.model.ParamModel;
import com.whx.router.compiler.doc.model.RouteModel;
import com.whx.router.compiler.doc.model.ServiceModel;
import com.whx.router.compiler.utils.Logger;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.StandardLocation;

public class DocumentUtils {

    private static Writer docWriter;
    private static DocumentModel documentModel;
    private static boolean isDocEnable;

    public static void createDoc(Filer mFiler, String moduleName, Logger logger, boolean isEnable) {
        isDocEnable = isEnable;
        if (!isDocEnable)
            return;
        try {
            documentModel = new DocumentModel();
            docWriter = mFiler.createResource(
                    StandardLocation.SOURCE_OUTPUT,
                    DOCS,
                    moduleName + "-" + DOCUMENT_FILE_NAME
            ).openWriter();
        } catch (IOException e) {
            logger.error(moduleName + " Failed to create the document because " + e.getMessage());
        }
    }

    public static void generateDoc(String moduleName, Logger logger) {
        if (!isDocEnable)
            return;
        try {
            docWriter.append(new GsonBuilder().setPrettyPrinting().create().toJson(documentModel));
            docWriter.flush();
            docWriter.close();
        } catch (IOException e) {
            logger.error(moduleName + " Failed to generate the document because " + e.getMessage());
        }
    }

    public static void addServiceDoc(String moduleName, Logger logger, Element element, Service service) {
        if (!isDocEnable)
            return;
        try {
            String className = ((TypeElement) element).getInterfaces().get(0).toString();
            String key = className.substring(className.lastIndexOf(".") + 1);
            if (!StringUtils.isEmpty(service.alias())) {
                key += "$" + service.alias();
            }
            documentModel.getServices().put(key, new ServiceModel(service.alias(), className, element.toString(), service.remark(), moduleName));
        } catch (Exception e) {
            logger.error(moduleName + " Failed to add service [" + element.toString() + "] document, " + e.getMessage());
        }
    }

    public static void addInterceptorDoc(String moduleName, Logger logger, Element element, Interceptor interceptor) {
        if (!isDocEnable)
            return;
        try {
            documentModel.getInterceptors().add(new InterceptorModel(interceptor.ordinal(), element.toString(), interceptor.remark(), moduleName));
        } catch (Exception e) {
            logger.error(moduleName + " Failed to add interceptor [" + element.toString() + "] document, " + e.getMessage());
        }
    }

    public static void addRouteGroupDoc(String moduleName, Logger logger, String group, List<RouteModel> routeModelList) {
        if (!isDocEnable)
            return;
        try {
            documentModel.getRoutes().put(group, routeModelList);
        } catch (Exception e) {
            logger.error(moduleName + " Failed to add route group [" + group + "] document, " + e.getMessage());
        }
    }

    public static void addRouteDoc(String moduleName, Logger logger, Element element, List<RouteModel> routeModelList, Route route, String typeDoc, Types types, TypeMirror serializableType, TypeMirror parcelableType) {
        if (!isDocEnable)
            return;
        try {
            RouteModel routeModel = new RouteModel();
            routeModel.setPath(route.path());
            routeModel.setType(typeDoc);
            routeModel.setPathClass(element.toString());
            if (!StringUtils.isEmpty(route.remark())) {
                routeModel.setRemark(route.remark());
            }
            if (route.tag() != 0) {
                routeModel.setTag(route.tag());
            }
            routeModel.setModuleName(moduleName);
            if (route.deprecated()) {
                routeModel.setDeprecated(true);
            }
            if (route.ignoreHelper()) {
                routeModel.setIgnoreHelper(true);
            }
            addParamCode(moduleName, logger, element, routeModel, types, serializableType, parcelableType);
            routeModelList.add(routeModel);
        } catch (Exception e) {
            logger.error(moduleName + " Failed to add route [" + element.toString() + "] document, " + e.getMessage());
        }
    }

    private static void addParamCode(String moduleName, Logger logger, Element element, RouteModel routeModel, Types types, TypeMirror serializableType, TypeMirror parcelableType) {
        List<ParamModel> tempParamModels = new ArrayList<>();
        for (Element field : element.getEnclosedElements()) {
            if (field.getKind().isField() && field.getAnnotation(Param.class) != null) {
                Param param = field.getAnnotation(Param.class);
                String paramName = field.getSimpleName().toString();
                TypeMirror typeMirror = field.asType();
                String typeStr = typeMirror.toString();

                ParamModel paramModel = new ParamModel();
                if (!StringUtils.isEmpty(param.remark())) {
                    paramModel.setRemark(param.remark());
                }
                paramModel.setRequired(param.required());
                paramModel.setType(typeStr);

                switch (typeStr) {
                    case BYTE_PACKAGE, BYTE_PRIMITIVE -> paramModel.setIntentType("withByte");
                    case SHORT_PACKAGE, SHORT_PRIMITIVE -> paramModel.setIntentType("withShort");
                    case INTEGER_PACKAGE, INTEGER_PRIMITIVE -> paramModel.setIntentType("withInt");
                    case LONG_PACKAGE, LONG_PRIMITIVE -> paramModel.setIntentType("withLong");
                    case FLOAT_PACKAGE, FLOAT_PRIMITIVE -> paramModel.setIntentType("withFloat");
                    case DOUBLE_PACKAGE, DOUBLE_PRIMITIVE -> paramModel.setIntentType("withDouble");
                    case BOOLEAN_PACKAGE, BOOLEAN_PRIMITIVE -> paramModel.setIntentType("withBoolean");
                    case CHAR_PACKAGE, CHAR_PRIMITIVE -> paramModel.setIntentType("withChar");
                    case STRING_PACKAGE -> paramModel.setIntentType("withString");
                    default -> {
                        if (types.isSubtype(typeMirror, parcelableType)) {
                            paramModel.setIntentType("withParcelable");
                        } else if (types.isSubtype(typeMirror, serializableType)) {
                            paramModel.setIntentType("withSerializable");
                        } else {
                            paramModel.setIntentType("withObject");
                        }
                    }
                }

                if (StringUtils.isEmpty(param.name()) && !param.required()) {
                    paramModel.setName(paramName);
                } else {
                    if (!StringUtils.isEmpty(param.name())) {
                        paramModel.setName(param.name());
                    } else {
                        paramModel.setName(paramName);
                    }
                }
                tempParamModels.add(paramModel);
            }
        }

        // The parent class parameter is processed before the subclass parameter
        if (!tempParamModels.isEmpty()) {
            tempParamModels.addAll(routeModel.getParamsType());
            routeModel.setParamsType(tempParamModels);
        }

        // if has parent?
        TypeMirror parent = ((TypeElement) element).getSuperclass();
        if (parent instanceof DeclaredType) {
            Element parentElement = ((DeclaredType) parent).asElement();
            if (parentElement instanceof TypeElement && !((TypeElement) parentElement).getQualifiedName().toString().startsWith("android")) {
                addParamCode(moduleName, logger, parentElement, routeModel, types, serializableType, parcelableType);
            }
        }
    }

}
