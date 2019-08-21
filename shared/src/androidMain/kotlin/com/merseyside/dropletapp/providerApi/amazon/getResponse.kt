package com.merseyside.dropletapp.providerApi.amazon

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

actual fun getRequest(
    method: String,
    service: String,
    host: String,
    region: String,
    endpoint: String,
    action: String,
    accessKey: String,
    secretKey: String
): Pair<String, Map<String, String>> {

    val request_parameters = "Action=$action&Version=2016-11-28"

    val date = Date()

    var sdf = SimpleDateFormat("YYYYMMDD'T'HHmmSS'Z'")
    val amz_date = sdf.format(date)

    sdf = SimpleDateFormat("YYYYMMDD")
    val date_stamp = sdf.format(date)

    /*Task 1*/

    val canonical_uri = '/'
    val canonical_querystring = request_parameters

    val canonical_headers = "host:$host\nx-amz-date$amz_date\n"

    val signed_headers = "host;x-amz-date"

    val body = when(method) {
        "GET" -> ""
        else -> request_parameters
    }

    val payload_hash = getSha256Hex(body)

    val canonical_request = "$method\n$canonical_uri\n$canonical_headers\n$signed_headers\n$payload_hash"

    /*Task 2*/

    val algorithm = "AWS4-HMAC-SHA256"
    val credential_scope = "$date_stamp/$region/$service/aws4_request"

    val string_to_sign = "$algorithm\n$amz_date\n$credential_scope\n${getSha256Hex(canonical_request)}"

    /*Task 3*/
    val signing_key = getSignatureKey(secretKey, date_stamp, region, service)

    val signature = HmacSHA256(signing_key, string_to_sign.toByteArray(StandardCharsets.UTF_8)).toHex()

    /*Task 4*/

    val authorization_header = "$algorithm Credential=$accessKey/$credential_scope, SignedHeaders=$signed_headers, Signature=$signature"

    val headers = mapOf<String, String>(
        "x-amz-date" to amz_date,
        "Authorization" to authorization_header
    ).toMutableMap()

//    if (method == "POST") {
//        headers["Content-Type"] = "application/x-amz-json-1.0"
//    }

    val request_url = "$endpoint?$canonical_querystring"

    return request_url to headers
}

private fun getSha256Hex(value: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val encodedHash = digest.digest(
        value.toByteArray(StandardCharsets.UTF_8)
    )

    return encodedHash.toHex()
}

fun ByteArray.toHex() = this.joinToString(separator = "") { it.toInt().and(0xff).toString(16).padStart(2, '0') }


@Throws(Exception::class)
private fun HmacSHA256(data: ByteArray, key: ByteArray): ByteArray {
    val algorithm = "HmacSHA256"
    val mac = Mac.getInstance(algorithm)
    mac.init(SecretKeySpec(key, algorithm))
    return mac.doFinal(data)
}

@Throws(Exception::class)
private fun getSignatureKey(key: String, dateStamp: String, regionName: String, serviceName: String): ByteArray {
    val kSecret = "AWS4$key".toByteArray(charset("UTF-8"))
    val kDate = HmacSHA256(dateStamp.toByteArray(StandardCharsets.UTF_8), kSecret)
    val kRegion = HmacSHA256(regionName.toByteArray(StandardCharsets.UTF_8), kDate)
    val kService = HmacSHA256(serviceName.toByteArray(StandardCharsets.UTF_8), kRegion)
    return HmacSHA256("aws4_request".toByteArray(StandardCharsets.UTF_8), kService)
}