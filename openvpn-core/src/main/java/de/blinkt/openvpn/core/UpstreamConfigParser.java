package de.blinkt.openvpn.core;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.blinkt.openvpn.VpnProfile;

import static de.blinkt.openvpn.core.ConfigParser.CONVERTED_PROFILE;

public class UpstreamConfigParser {

    private final static String TAG = "UpstreamConfigParser";

    private static final Pattern TAG_REGEX = Pattern.compile("<tag>(.+?)</tag>");

    public static VpnProfile parseConfig(Context context, String configFileBody) {

        byte[] data = configFileBody.getBytes();
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(data));

        ConfigParser cp = new ConfigParser();
        try {
            cp.parseConfig(reader);
            VpnProfile vpnProfile = cp.convertProfile();

            ProfileManager.getInstance(context).addProfile(vpnProfile);

            return vpnProfile;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConfigParser.ConfigParseError configParseError) {
            configParseError.printStackTrace();
        }

        VpnProfile vpnProfile = new VpnProfile(CONVERTED_PROFILE);
        HashMap<String, String> configMap = new HashMap<>();
        BufferedReader br = new BufferedReader(reader);
        try {
            while (true) {
                String line = br.readLine();

                if (line == null)
                    break;

                String[] splits = line.split("\\s+", 2);

                if (splits.length == 1) {
                    configMap.put(splits[0], "");
                } else {
                    configMap.put(splits[0], splits[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (configMap.containsKey("client") || configMap.containsKey("pull")) {
            vpnProfile.mUsePull = true;
        }

        if (configMap.containsKey("nobind")) {
            vpnProfile.mNobind = true;
        }

        if (configMap.containsKey("dev")) {
            if (configMap.get("dev").equals("tun")) {}
            else
                throw new IllegalArgumentException("Only dev option supports");
        } else {
            throw new IllegalArgumentException("Only dev option supports2");
        }

        if (configMap.containsKey("remote-cert-tls")) {
            if (configMap.get("remote-cert-tls").equals("server"))
                vpnProfile.mExpectTLSCert = true;
        }

        if (configMap.containsKey("remote")) {
            String[] remoteSettings = configMap.get("remote").split(" ");
            if (remoteSettings.length == 3) {
                Connection connection = new Connection();
                connection.mServerName = remoteSettings[0];
                connection.mServerPort = remoteSettings[1];
                if (!remoteSettings[2].equals("udp"))
                    connection.mUseUdp = false;

                vpnProfile.mConnections = new Connection[] {connection};
            }
        } else
            throw new IllegalArgumentException("No connection settings");

        parseCertificates(context, vpnProfile, configFileBody);

        //Log.d(TAG, "vpnProfile = " + vpnProfile.getConfigFile(context, false));

        ProfileManager.getInstance(context).addProfile(vpnProfile);

        return vpnProfile;
    }

    private static void parseCertificates(Context context, VpnProfile vpnProfile, String configFileBody) {

        CacheFileManager fileManager = new CacheFileManager();
        String[] crts = new String[] {"key", "cert", "ca", "tls-auth"};

        for (String crt : crts) {

            Pattern TAG_REGEX = Pattern.compile("<" + crt + ">(.+?)</" + crt + ">", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

            Matcher matcher = TAG_REGEX.matcher(configFileBody);
            if (matcher.find()) {
                File file = new File(context.getCacheDir().getAbsolutePath(), crt + ".crt");
                fileManager.writeToFile(file, matcher.group(1));

                if (crt.equals(crts[0]))
                    vpnProfile.mClientKeyFilename = file.getName();
                else if(crt.equals(crts[1]))
                    vpnProfile.mClientCertFilename = file.getName();
                else if (crt.equals(crts[2]))
                    vpnProfile.mCaFilename = file.getName();
                else
                    vpnProfile.mTLSAuthFilename = file.getName();

            } else {
                Log.d(TAG, "not found");
            }
        }
    }
}
