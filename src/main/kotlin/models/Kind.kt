package models

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.util.*

enum class KindType(val type : String) {
    HOST("Host"),
    SOFTWAREINSTANCE("SoftwareInstance"),
    SOFTWARECOMPONENT("SoftwareComponent"),
    LOADBALANCERMEMBER("LoadBalancerMember"),
    LOADBALANCERPOOL("LoadBalancerPool"),
    LOADBALANCERSERVICE("LoadBalancerService"),
    LOADBALANCERINSTANCE("LoadBalancerInstance"),
    VIRTUALMACHINE("VirtualMachine"),
    DATABASE("Database"),
//Infrastructure
    FILESYSTEM("FileSystem"),
    HARDWARECONTAINER("HardwareContainer"),
    SUBNET("Subnet"),
    NETWORKDEVICE("NetworkDevice"),
    DISKDRIVE("DiskDrive")
}
data class Kind(
    @SerializedName("#InferredElement:Inference:Associate:DiscoveryAccess.endpoint")  var endpoint: String = "",
    @SerializedName("#id") var id : String = "",
    @SerializedName("cloud") var cloud : Boolean? = false,
    @SerializedName("name") var name : String = "",
    @SerializedName("os") var os : String = "",
    @SerializedName("partition") var partition : Boolean? = false,
    @SerializedName("vendor") var vendor : String? = "",
    @SerializedName("virtual") var virtual : Boolean? = false
)
{
//    constructor() :  this(endpoint="", id="", cloud = false, name = "", os = "", partition = false, vendor = "", virtual = true)
    companion object {
        fun fromMapToKind(data: Any): Kind {
            val gson = GsonBuilder().create()
            return gson.fromJson(gson.toJson(data), Kind::class.java)
        }
    }
}

