package com.merseyside.dropletapp.ssh

import kotlinx.serialization.Serializable

@Serializable
sealed class ConnectionType {

    abstract fun getSetupScript(): String
    abstract fun isNeedsConfig(): Boolean

    open fun getConfigFileScript(): String {
        return ""
    }

    @Serializable
    class OpenVpnType(private val username: String) : ConnectionType() {
        override fun getSetupScript(): String {

            return "export CLIENT=$username" +
                    " && bash -c " +
                    "\"$(wget https://gist.githubusercontent.com/myvpn-run/ab573e451a7b44991fb3a45" +
                    "66496d0f0/raw/4b9aa9f10049f1350fd81e1d1e4350b5bb227c7e/openvpn.sh -O -)\""

        }

        override fun isNeedsConfig(): Boolean {
            return true
        }

        override fun getConfigFileScript(): String {
            return "cat /root/$username.ovpn"
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

    }

    @Serializable
    class WireGuardType : ConnectionType() {
        override fun getSetupScript(): String {
            return "export VPN_CLIENT_CONFIG_FILE=/tmp/client.conf && bash -c " +
                    "\"$(wget https://gist.githubusercontent.com/my0419/dd0111d60375dc756c19a70e0907e32b/" +
                    "raw/05ca85bf110199ab35421e7702bf6bc184323603/wireguard.sh -O -)\""
        }

        override fun isNeedsConfig(): Boolean {
            return true
        }

        override fun getConfigFileScript(): String {
            return "cat /tmp/client.conf"
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

    }

    @Serializable
    class L2TPType(private val userName: String, private val password: String, private val key: String) : ConnectionType() {
        override fun getSetupScript(): String {
            return "export VPN_IPSEC_PSK=$key && export VPN_USER=$userName && export VPN_PASSWORD=$password &&" +
                    " bash -c \"$(wget https://gist.githubusercontent.com/myvpn-run/c2ba3ac57c290" +
                    "a15f6808affc8419edf/raw/17fda843f3ad2ddaebc8ceaa9197a2a89f75ee4d/l2tp.sh -O -)\""
        }

        override fun isNeedsConfig(): Boolean {
            return false
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

    }

    @Serializable
    class PPTPType(private val userName: String, private val password: String) : ConnectionType() {
        override fun getSetupScript(): String {
            return "export CLIENT=$userName && export PASS=$password && bash -c" +
                    " \"$(wget https://gist.githubusercontent.com/myvpn-run/" +
                    "5681e9085b2f0e9bef7c435746b99462/raw/5a9b42cb6d2df3f2fc2637ab83a025859191e88d/pptp.sh -O -)\""
        }

        override fun isNeedsConfig(): Boolean {
            return false
        }

        override fun equals(other: Any?): Boolean {
            return this === other
        }

    }
}