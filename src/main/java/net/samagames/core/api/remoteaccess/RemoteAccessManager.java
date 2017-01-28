package net.samagames.core.api.remoteaccess;

import net.samagames.core.api.remoteaccess.annotations.RemoteAttribute;
import net.samagames.core.api.remoteaccess.annotations.RemoteMethod;
import net.samagames.core.api.remoteaccess.annotations.RemoteObject;
import net.samagames.core.api.remoteaccess.annotations.RemoteParameter;

import javax.management.*;
import javax.management.modelmbean.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * ╱╲＿＿＿＿＿＿╱╲
 * ▏╭━━╮╭━━╮▕
 * ▏┃＿＿┃┃＿＿┃▕
 * ▏┃＿▉┃┃▉＿┃▕
 * ▏╰━━╯╰━━╯▕
 * ╲╰╰╯╲╱╰╯╯╱  Created by Silvanosky on 28/07/2016
 * ╱╰╯╰╯╰╯╰╯╲
 * ▏▕╰╯╰╯╰╯▏▕
 * ▏▕╯╰╯╰╯╰▏▕
 * ╲╱╲╯╰╯╰╱╲╱
 * ＿＿╱▕▔▔▏╲＿＿
 * ＿＿▔▔＿＿▔▔＿＿
 */
public class RemoteAccessManager {

    private MBeanServer mBeanServer;

    public RemoteAccessManager()
    {
        mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    public boolean registerMBean(Object service) throws Exception {

        ObjectName name = null;
        try {
            Class<?> sClass = service.getClass();
            name = new ObjectName(sClass.getName() + ":type=OpenMXBean,name=" + sClass.getSimpleName());

            RequiredModelMBean modelMBean = new RequiredModelMBean(generateMBeanInfo(service));
            modelMBean.setManagedResource(service, "objectReference");
            mBeanServer.registerMBean(modelMBean, name);

            System.out.println("Register " + name.toString());
            return true;
        } catch (MalformedObjectNameException
                | NullPointerException
                | InstanceAlreadyExistsException
                | NotCompliantMBeanException
                | InstanceNotFoundException
                | RuntimeOperationsException
                | InvalidTargetObjectTypeException
                | MBeanException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private ModelMBeanInfo generateMBeanInfo(Object service) throws Exception {
        Class<?> sClass = service.getClass();
        if (!sClass.isAnnotationPresent(RemoteObject.class))
        {
            throw new Exception("Object " + sClass.getName() + " doesn't have RemoteObject annotation");
        }
        RemoteObject remoteObject = sClass.getAnnotation(RemoteObject.class);

        return new ModelMBeanInfoSupport(
                sClass.getName(),
                remoteObject.description(),
                generateAttributesInfos(sClass),
                null, //We don't need constructor only casino call
                generateOperationsInfos(sClass),
                null);
    }

    private ModelMBeanAttributeInfo[] generateAttributesInfos(Class sClass)
    {
        ArrayList<ModelMBeanAttributeInfo> attributes = new ArrayList<>();
        for (Field field : sClass.getFields())
        {
            if (!field.isAnnotationPresent(RemoteAttribute.class))
            {
                continue;
            }

            RemoteAttribute remoteAttribute = field.getAnnotation(RemoteAttribute.class);
            List<String> desc = new ArrayList<>();

            desc.add("name=" + remoteAttribute.name());
            desc.add("displayName=" + remoteAttribute.displayName());
            desc.add("descriptorType=attribute");
            desc.add("default=" + remoteAttribute.defaultValue());

            String value = remoteAttribute.setMethod();
            if (!value.equals(""))
            {
                desc.add("setMethod=" +value);
            }

            value = remoteAttribute.getMethod();
            if (!value.equals(""))
            {
                desc.add("getMethod=" +value);
            }
            //Could be simplified by check get and set before allocate the array but boring
            Descriptor descriptor = new DescriptorSupport(desc.toArray(new String[desc.size()]));
            ModelMBeanAttributeInfo valeur = new ModelMBeanAttributeInfo(
                    remoteAttribute.name(),
                    field.getType().getName(),
                    remoteAttribute.displayName(),
                    remoteAttribute.isReadable(),
                    remoteAttribute.isWritable(),
                    (!value.equals("") && value.startsWith("is")),
                    descriptor);
            attributes.add(valeur);
        }

        if (attributes.size() > 0)
        {
            return attributes.toArray(new ModelMBeanAttributeInfo[attributes.size()]);
        }else {
            //No attribute so return null is better
            return null;
        }
    }

    private ModelMBeanOperationInfo[] generateOperationsInfos(Class sClass)
    {
        ArrayList<ModelMBeanOperationInfo> operations = new ArrayList<>();
        for (Method method : sClass.getMethods())
        {
            if (!method.isAnnotationPresent(RemoteMethod.class))
            {
                continue;
            }

            RemoteMethod remoteMethod = method.getAnnotation(RemoteMethod.class);

            MBeanParameterInfo[] parameterInfos = null;
            if (method.getParameters().length > 0)
            {
                parameterInfos = new MBeanParameterInfo[method.getParameters().length];
                int i = 0;
                for (Parameter parameter : method.getParameters())
                {
                    String name = parameter.getName();
                    String description = parameter.getName();
                    if (parameter.isAnnotationPresent(RemoteParameter.class))
                    {
                        RemoteParameter remoteParameter = parameter.getAnnotation(RemoteParameter.class);
                        name = remoteParameter.name();
                        description = remoteParameter.description();
                    }

                    parameterInfos[i++] = new MBeanParameterInfo(name, parameter.getType().getName(), description);
                }
            }
            ModelMBeanOperationInfo operation = new ModelMBeanOperationInfo(
                    method.getName(),
                    remoteMethod.description(),
                    parameterInfos,
                    method.getReturnType().getSimpleName(),
                    remoteMethod.impact());

            operations.add(operation);
        }

        if (operations.size() > 0)
        {
            return operations.toArray(new ModelMBeanOperationInfo[operations.size()]);
        }else {
            //No attribute so return null is better
            return null;
        }
    }

    //Example without generation
    private static ModelMBeanInfo creerMBeanInfo() {
        //Attribues
        Descriptor descriptorValeur = new DescriptorSupport(
                "name=Valeur", "descriptorType=attribute", "default=0",
                "displayName=Valeur stockée dans la classe", "getMethod=getValeur",
                "setMethod=setValeur");

        Descriptor descriptorNom = new DescriptorSupport(
                "name=Nom",
                "descriptorType=attribute",
                "displayName=Nom de la classe",
                "getMethod=getNom");

        ModelMBeanAttributeInfo[] mmbai = new ModelMBeanAttributeInfo[]{
                new ModelMBeanAttributeInfo("Valeur", "java.lang.Integer",
                        "Valeur stockée dans la classe", true, true, false, descriptorValeur),
                new ModelMBeanAttributeInfo("Nom", "java.lang.String",
                        "Nom de la classe", true, false, false, descriptorNom)
        };

        // Operations
        ModelMBeanOperationInfo[] mmboi = new ModelMBeanOperationInfo[4];

        mmboi[0] = new ModelMBeanOperationInfo("getValeur",
                "getter pour l'attribut Valeur", null, "Integer",
                ModelMBeanOperationInfo.INFO);

        MBeanParameterInfo[] mbpiSetValeur = new MBeanParameterInfo[1];
        mbpiSetValeur[0] = new MBeanParameterInfo("valeur", "java.lang.Integer",
                "valeur de l'attribut");
        mmboi[1] = new ModelMBeanOperationInfo("setValeur",
                "setter pour l'attribut Valeur", mbpiSetValeur, "void",
                ModelMBeanOperationInfo.ACTION);

        mmboi[2] = new ModelMBeanOperationInfo("getNom",
                "getter pour l'attribut Nom", null, "String",
                ModelMBeanOperationInfo.INFO);

        mmboi[3] = new ModelMBeanOperationInfo("rafraichir",
                "Rafraichir les données", null, "void", ModelMBeanOperationInfo.ACTION);

        //Constructeur
        ModelMBeanConstructorInfo[] mmbci = new ModelMBeanConstructorInfo[1];
        mmbci[0] = new ModelMBeanConstructorInfo("MaClasse",
                "Constructeur par défaut", null);

        return new ModelMBeanInfoSupport("com.jmdoudoux.tests.jmx.MaClasse",
                "Exemple de ModelBean", mmbai, mmbci, mmboi, null);
    }
}
