/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.codegen;

import java.util.HashMap;
import java.util.Map;

import com.mysema.codegen.model.ClassType;
import com.mysema.codegen.model.SimpleType;
import com.mysema.codegen.model.Type;
import com.mysema.codegen.model.TypeCategory;
import com.mysema.codegen.model.TypeExtends;
import com.mysema.query.types.TemplateExpression;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Path;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.expr.DateTimeExpression;
import com.mysema.query.types.expr.EnumExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.expr.TimeExpression;
import com.mysema.query.types.path.BooleanPath;
import com.mysema.query.types.path.ComparablePath;
import com.mysema.query.types.path.DatePath;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.EnumPath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.SimplePath;
import com.mysema.query.types.path.StringPath;
import com.mysema.query.types.path.TimePath;
import com.mysema.query.types.template.BooleanTemplate;
import com.mysema.query.types.template.ComparableTemplate;
import com.mysema.query.types.template.DateTemplate;
import com.mysema.query.types.template.DateTimeTemplate;
import com.mysema.query.types.template.EnumTemplate;
import com.mysema.query.types.template.NumberTemplate;
import com.mysema.query.types.template.SimpleTemplate;
import com.mysema.query.types.template.StringTemplate;
import com.mysema.query.types.template.TimeTemplate;

/**
 * TypeMappings defines mappings from Java types to {@link Expression}, {@link Path} and {@link TemplateExpression} types
 * 
 * @author tiwe
 *
 */
public class TypeMappings {

    private final Map<TypeCategory, ClassType> customTypes = new HashMap<TypeCategory, ClassType>();

    private final Map<TypeCategory, ClassType> exprTypes = new HashMap<TypeCategory, ClassType>();

    private final Map<TypeCategory, ClassType> pathTypes = new HashMap<TypeCategory, ClassType>();

    public TypeMappings(){
        register(TypeCategory.STRING, StringExpression.class, StringPath.class, StringTemplate.class);
        register(TypeCategory.BOOLEAN, BooleanExpression.class, BooleanPath.class, BooleanTemplate.class);
        register(TypeCategory.COMPARABLE, ComparableExpression.class, ComparablePath.class, ComparableTemplate.class);
        register(TypeCategory.ENUM, EnumExpression.class, EnumPath.class, EnumTemplate.class);
        register(TypeCategory.DATE, DateExpression.class, DatePath.class, DateTemplate.class);
        register(TypeCategory.DATETIME, DateTimeExpression.class, DateTimePath.class, DateTimeTemplate.class);
        register(TypeCategory.TIME, TimeExpression.class, TimePath.class, TimeTemplate.class);
        register(TypeCategory.NUMERIC, NumberExpression.class, NumberPath.class, NumberTemplate.class);

        register(TypeCategory.ARRAY, Expression.class, SimplePath.class, SimpleTemplate.class);
        register(TypeCategory.COLLECTION, Expression.class, SimplePath.class, SimpleTemplate.class);
        register(TypeCategory.SET, Expression.class, SimplePath.class, SimpleTemplate.class);
        register(TypeCategory.LIST, Expression.class, SimplePath.class, SimpleTemplate.class);
        register(TypeCategory.MAP, Expression.class, SimplePath.class, SimpleTemplate.class);
        register(TypeCategory.SIMPLE, Expression.class, SimplePath.class, SimpleTemplate.class);

        register(TypeCategory.CUSTOM, Expression.class, Path.class, SimpleTemplate.class);
        register(TypeCategory.ENTITY, Expression.class, Path.class, SimpleTemplate.class);
    }

    public Type getCustomType(Type type, EntityType model, boolean raw){
        return getCustomType(type, model, raw, false, false);
    }

    public Type getCustomType(Type type, EntityType model, boolean raw, boolean rawParameters, boolean extend){
        return getQueryType(customTypes, type, model, raw, rawParameters, extend);
    }

    public Type getExprType(Type type, EntityType model, boolean raw){
        return getExprType(type, model, raw, false, false);
    }

    public Type getExprType(Type type, EntityType model, boolean raw, boolean rawParameters, boolean extend){
        return getQueryType(exprTypes, type, model, raw, rawParameters, extend);
    }

    public Type getPathType(Type type, EntityType model, boolean raw){
        return getPathType(type, model, raw, false, false);
    }

    public Type getPathType(Type type, EntityType model, boolean raw, boolean rawParameters, boolean extend){
        return getQueryType(pathTypes, type, model, raw, rawParameters, extend);
    }

    private Type getQueryType(Map<TypeCategory, ClassType> types, Type type, EntityType model, boolean raw, boolean rawParameters, boolean extend){
        Type exprType = types.get(type.getCategory());
        return getQueryType(type, model, exprType, raw, rawParameters, extend);
    }

    public Type getQueryType(Type type, EntityType model, Type exprType, boolean raw, boolean rawParameters, boolean extend){
        TypeCategory category = type.getCategory();
        if (raw && category != TypeCategory.ENTITY && category != TypeCategory.CUSTOM){
            return exprType;
            
        }else if (category == TypeCategory.STRING || category == TypeCategory.BOOLEAN){
            return exprType;

        }else if (category == TypeCategory.ENTITY || category == TypeCategory.CUSTOM){
            String packageName = type.getPackageName();
            String simpleName;
            if (type.getPackageName().isEmpty()){
                simpleName = model.getPrefix()+type.getFullName().replace('.', '_');
                return new SimpleType(category, simpleName, "", simpleName, false, false);
            }else{                
                simpleName = model.getPrefix()+type.getFullName().substring(packageName.length()+1).replace('.', '_');
                return new SimpleType(category, packageName+"."+simpleName, packageName, simpleName, false, false);
            }

        }else{    
            if (rawParameters){
                type = new SimpleType(type);
            }
            if (!type.isFinal() && extend){
                type = new TypeExtends(type);
            }
            return new SimpleType(exprType, type);
            
        }
    }
    
    @SuppressWarnings("unchecked")
    public void register(TypeCategory category,
            Class<? extends Expression> expr,
            Class<? extends Path> path,
            Class<? extends TemplateExpression> custom){
        exprTypes.put(category, new ClassType(expr));
        pathTypes.put(category, new ClassType(path));
        customTypes.put(category, new ClassType(custom));
    }

}
