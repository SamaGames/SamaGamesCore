package net.samagames.generator;

import com.squareup.javapoet.*;
import net.samagames.api.stats.IPlayerStats;
import net.samagames.api.stats.IStatsManager;
import net.samagames.persistanceapi.beans.players.GroupsBean;
import net.samagames.persistanceapi.beans.players.PlayerBean;
import net.samagames.persistanceapi.beans.statistics.PlayerStatisticsBean;

import javax.lang.model.element.Modifier;
import java.beans.ConstructorProperties;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Silvanosky on 25/03/2016.
 */
public class Generator {

    private static List<JavaFile> toBuild = new ArrayList<>();

    public static void main(String[] args)
    {
        createPlayerStat();

        createCacheLoader();

        build();
    }

    public static void createPlayerStat()
    {
        List<JavaFile> typeStats = loadGameStats();
        ClassName apimpl = ClassName.get("net.samagames.core", "ApiImplementation");
        TypeSpec.Builder playerStatsBuilder = TypeSpec.classBuilder("PlayerStats")
                .addModifiers(Modifier.PUBLIC);
        playerStatsBuilder.addSuperinterface(IPlayerStats.class);

        playerStatsBuilder.addField(UUID.class, "playerUUID", Modifier.PRIVATE);
        playerStatsBuilder.addField(apimpl, "api", Modifier.PRIVATE);
        playerStatsBuilder.addField(boolean[].class, "statsToLoad", Modifier.PRIVATE);

        for (JavaFile javaFile : typeStats)
        {
            playerStatsBuilder.addField(ClassName.get(javaFile.packageName, javaFile.typeSpec.name),
                    javaFile.typeSpec.name.toLowerCase(), Modifier.PRIVATE);
        }
        ClassName playerData = ClassName.get("net.samagames.core.api.player", "PlayerData");
        ClassName statsNamesCLass = ClassName.get("net.samagames.api.stats.IStatsManager", "StatsNames");
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(apimpl, "api")
                .addParameter(playerData, "player")
                .addParameter(boolean[].class, "statsToLoad")
                .addStatement("this.$N = $N", "api", "api")
                .addStatement("this.$N = $N.getPlayerID()", "playerUUID", "player")
                .addStatement("this.$N = $N", "statsToLoad", "statsToLoad");
        constructor.addStatement("boolean global = statsToLoad[" + IStatsManager.StatsNames.GLOBAL.intValue() + "]");
        for (JavaFile javaFile : typeStats)
        {
            double value = 0;
            IStatsManager.StatsNames stat = null;
            for (IStatsManager.StatsNames statsNames : IStatsManager.StatsNames.values())
            {
                double sim = similarity(statsNames.name().toLowerCase(), javaFile.typeSpec.name.toLowerCase());
                if ( sim > value )
                {
                    value = sim;
                    stat = statsNames;
                }
            }
            constructor.addStatement("if (global || statsToLoad[" + stat.intValue() + "]) \n"
                                + "this." + javaFile.typeSpec.name.toLowerCase() + " = new " + javaFile.typeSpec.name + "(player)");
        }
        playerStatsBuilder.addMethod(constructor.build());

        MethodSpec getapi = MethodSpec.methodBuilder("getApi")
                .addModifiers(Modifier.PUBLIC)
                .returns(apimpl)
                .addStatement("return $N", "api")
                .build();
        playerStatsBuilder.addMethod(getapi);

        MethodSpec getplayer = MethodSpec.methodBuilder("getPlayerUUID")
                .addModifiers(Modifier.PUBLIC)
                .returns(UUID.class)
                .addStatement("return $N", "playerUUID")
                .build();
        playerStatsBuilder.addMethod(getplayer);

        MethodSpec.Builder upStat = MethodSpec.methodBuilder("updateStats")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(void.class);
        for (JavaFile javaFile : typeStats)
        {
            String variable = javaFile.typeSpec.name.toLowerCase();
            String code = "if (" + variable + " != null)\n" +
                    "   " + variable + ".update()";
            upStat.addStatement(code);

        }
        playerStatsBuilder.addMethod(upStat.build());


        MethodSpec.Builder refreshStat = MethodSpec.methodBuilder("refreshStats")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(boolean.class);

        for (JavaFile javaFile : typeStats)
        {
            String variable = javaFile.typeSpec.name.toLowerCase();
            String code = "if (" + variable + " != null)\n" +
                    "   " + variable + ".refresh()";
            refreshStat.addStatement(code);

        }
        refreshStat.addStatement("return true");
        playerStatsBuilder.addMethod(refreshStat.build());

        for (JavaFile javaFile : typeStats)
        {
            ClassName className = ClassName.get(javaFile.packageName, javaFile.typeSpec.name);
            String variable = javaFile.typeSpec.name.toLowerCase();
            MethodSpec.Builder getter = MethodSpec.methodBuilder("get" + javaFile.typeSpec.name)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(className);

            getter.addStatement("return $N", variable);
            playerStatsBuilder.addMethod(getter.build());

            MethodSpec.Builder setter = MethodSpec.methodBuilder("set" + javaFile.typeSpec.name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(className, variable);

            setter.addStatement("this.$N = $N", variable, variable);
            playerStatsBuilder.addMethod(setter.build());

        }

        toBuild.add(JavaFile.builder("net.samagames.core.api.stats", playerStatsBuilder.build()).build());
    }

    public static List<JavaFile> loadGameStats()
    {
        ClassName apimpl = ClassName.get("net.samagames.core", "ApiImplementation");
        List<JavaFile> stats = new ArrayList<>();
        Field[] playerStatisticFields = PlayerStatisticsBean.class.getDeclaredFields();
        for (Field field : playerStatisticFields)
        {
            field.setAccessible(true);
            Class workingField = field.getType();
            String name = workingField.getSimpleName().replaceAll("Bean", "");

            TypeSpec.Builder object = TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get("net.samagames.api.stats.games", "I"+name))
                    .superclass(workingField);
            ClassName pdata = ClassName.get("net.samagames.core.api.player","PlayerData");
            MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(pdata, "playerData")
                    .addParameter(workingField, "bean");

            String sup = "super(playerData.getPlayerID()\n";

            Constructor constructor1 = field.getType().getConstructors()[0];
            if (!constructor1.isAnnotationPresent(ConstructorProperties.class))
                continue;
            ConstructorProperties annotation = (ConstructorProperties) constructor1.getAnnotation(ConstructorProperties.class);

            //Not efficiency but do the job and don't care for compilation
            Method[] subDeclaredMethods = workingField.getDeclaredMethods();
            int i = 0;
            for (String parameterName : annotation.value())
            {
                //Don't do the first iteration
                if (i == 0)
                {
                    i++;
                    continue;
                }
                double similitudeMax = 0.0;
                String methodName = "";
                for (Method method : subDeclaredMethods)
                {
                    if (method.getName().startsWith("get"))
                    {
                        double similitude = similarity(parameterName.toLowerCase(), method.getName().toLowerCase());
                        if (similitude > similitudeMax)
                        {
                            similitudeMax = similitude;
                            methodName = method.getName();
                        }
                    }
                }
                sup += ",bean." + methodName + "()\n";
            }
            sup += ")";

            constructor.addStatement(sup);
            constructor.addStatement("this.api = ($T) $T.get()", apimpl, apimpl);
            constructor.addStatement("this.$N = $N", "playerData", "playerData");

            object.addMethod(constructor.build());


            MethodSpec.Builder constructor2 = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(pdata, "playerData");

            String instruction = "super(playerData.getPlayerID()\n";

            boolean lol = true;
            for (Parameter parameter : field.getType().getConstructors()[0].getParameters())
            {
                if (lol)
                {
                    lol = false;
                    continue;
                }

                if (parameter.getType().equals(int.class))
                {
                    instruction += ",0\n";
                }else if (parameter.getType().equals(long.class))
                {
                    instruction += ",0\n";
                }else if (parameter.getType().equals(double.class))
                {
                    instruction += ",0.0\n";
                }else if (parameter.getType().equals(boolean.class))
                {
                    instruction += ",false\n";
                }else
                {
                    instruction += ", null\n";
                }
            }
            instruction += ")";

            constructor2.addStatement(instruction);
            constructor2.addStatement("this.api = ($T) $T.get()", apimpl, apimpl);
            constructor2.addStatement("this.$N = $N", "playerData", "playerData");

            object.addMethod(constructor2.build());


            object.addField(pdata, "playerData", Modifier.PRIVATE);
            object.addField(apimpl, "api", Modifier.PRIVATE);

            MethodSpec.Builder update = MethodSpec.methodBuilder("update")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addAnnotation(Override.class);
            ClassName jedis = ClassName.get("redis.clients.jedis", "Jedis");
            ClassName converter = ClassName.get("net.samagames.tools", "TypeConverter");
            update.addStatement("$T jedis = this.api.getBungeeResource()", jedis);

            Method[] declaredMethods = workingField.getDeclaredMethods();

            for (Method getters : declaredMethods)
            {
                if (getters.getName().startsWith("get"))
                {
                    update.addStatement("jedis.hset(\"statistic:\" + playerData.getPlayerID() + \":"
                            + workingField.getSimpleName() +"\", \"" + getters.getName().substring(3) + "\", \"\" + "
                            + getters.getName() + "())");
                }
            }

            update.addStatement("jedis.close()");
            object.addMethod(update.build());

            MethodSpec.Builder refresh = MethodSpec.methodBuilder("refresh")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addAnnotation(Override.class);

            refresh.addStatement("$T jedis = this.api.getBungeeResource()", jedis);

            for (Method getters : workingField.getDeclaredMethods())
            {
                if (getters.getName().startsWith("set"))
                {
                    refresh.addStatement(getters.getName() + "($T.convert(" + getters.getParameters()[0].getType().getName()
                            + ".class, jedis.hget(\"statistic:\" + playerData.getPlayerID() + \":"
                            + workingField.getSimpleName() +"\", \"" + getters.getName().substring(3) + "\")))", converter);
                }
            }

            refresh.addStatement("jedis.close()");
            object.addMethod(refresh.build());

            TypeSpec build = object.build();
            JavaFile file = JavaFile.builder("net.samagames.core.api.stats.games", build).build();
            stats.add(file);
            toBuild.add(file);
        }
        return stats;
    }

    public static void createCacheLoader()
    {
        //Imports
        ClassName jedis = ClassName.get("redis.clients.jedis", "Jedis");
        ClassName converter = ClassName.get("net.samagames.tools", "TypeConverter");

        //Generation
        TypeSpec.Builder object = TypeSpec.classBuilder("CacheLoader")
                .addModifiers(Modifier.PUBLIC);

        List<Class> toCreate = new ArrayList<>();
        toCreate.add(GroupsBean.class);
        toCreate.add(PlayerBean.class);

        //Loaders
        for (Class classe : toCreate)
        {
            MethodSpec.Builder getter = MethodSpec.methodBuilder("load")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(jedis, "jedis")
                    .addParameter(String.class, "key")
                    .addParameter(classe, "objet")
                    .returns(void.class);

            for (Method method : classe.getDeclaredMethods())
            {
                if (method.getName().startsWith("set"))
                {
                    getter.addStatement("objet." + method.getName() + "($T.convert("
                            + method.getParameters()[0].getType().getName() + ".class, jedis.hget(key, \""
                            + method.getName().substring(3) + "\")))", converter);
                }
            }
            object.addMethod(getter.build());
        }

        //Setters
        for (Class classe : toCreate)
        {
            MethodSpec.Builder setter = MethodSpec.methodBuilder("send")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(jedis, "jedis")
                    .addParameter(String.class, "key")
                    .addParameter(classe, "objet")
                    .returns(void.class);

            for (Method method : classe.getDeclaredMethods())
            {
                if (method.getName().startsWith("get"))
                {
                    setter.addStatement("jedis.hset(key, \"" + method.getName().substring(3) + "\","
                            + " \"\" + objet." + method.getName() + "())");
                }
            }
            object.addMethod(setter.build());
        }

        JavaFile file = JavaFile.builder("net.samagames.core.utils", object.build()).build();
        toBuild.add(file);
    }

    public static void build()
    {
        try {
            File file = new File("./Generation");
            file.delete();
            for (JavaFile javaFile : toBuild)
            {
                javaFile.writeTo(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MethodSpec getMethod(String name, TypeName retur)
    {
        MethodSpec.Builder getter = MethodSpec.methodBuilder(name);
        getter.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        getter.returns(retur);
        return getter.build();
    }

    public static MethodSpec getMethod(String name, Type retur)
    {
        return getMethod(name, TypeName.get(retur));
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
        /* // If you have StringUtils, you can use it to calculate the edit distance:
        return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
                                                             (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://r...content-available-to-author-only...e.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}
