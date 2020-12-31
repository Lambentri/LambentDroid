package io.gmp.does.lambent.droid

import java.time.LocalDate

data class MachineQuery(
    val machines: Map<String, Machine>,
    val speed_enum: Map<String, String>
) {
    companion object {
        fun fromNetwork(map: Map<String, String>) = object {
            val machines = map["machines"] as Map<String, Map<String, String>>
            val machines_ds = machines.map {
                it.key to Machine.fromNetwork(it.value)
            }.toMap()
            val speed_enum = map["speed_enum"] as Map<String, String>

            val data = MachineQuery(
                machines = machines_ds,
                speed_enum = speed_enum
            )
        }.data
    }
}

data class Machine(
    val name: String,
    val running: RunningEnum,
    val id: String,
    val desc: String,
    val speed: TickEnum = TickEnum.ONES,
    val iname: String?
) {
    companion object {
        fun fromNetwork(map: Map<String, String>) = object {
            val name: String by map
            val running: String by map
            val id: String by map
            val desc: String by map
            val speed: String by map
            val iname: String? by map

            val data = Machine(
                name,
                RunningEnum.valueOf(running),
                id,
                desc,
                TickEnum.valueOf(speed),
                iname
            )
        }.data
    }
}

data class Device(
    val iname: String,
    val id: String,
    val name: String,
    val bpp: BPP = BPP.RGB,
    val updated: LocalDate? = null
) {
    companion object {
        fun fromNetwork(map: Map<String, String>) = object {
            val iname: String by map
            val id: String by map
            val name: String by map
            val bpp: String by map
            val updated: String by map

            val data = Device(iname, id, name, BPP.valueOf(bpp))
        }.data
    }
}

data class LinkSpecSrc(
    val list_name: String,
    val ttl: String,
    val id: String,
    val cls: String
) {
    companion object {
        fun fromNetwork(map: Map<String, Any>) = object {
            val list_name: String by map
            val ttl: String by map
            val id: String by map
            val cls: String by map

            val data = LinkSpecSrc(list_name, ttl, id, cls)
        }.data
    }
}

data class LinkSpecTgt(
    val list_name: String,
    val grp: String,
    val iname: String,
    val id: String,
    val name: String
) {
    companion object {
        fun fromNetwork(map: Map<String, Any>) = object {
            val list_name: String by map
            val grp: String by map
            val iname: String by map
            val id: String by map
            val name: String by map

            val data = LinkSpecTgt(list_name, grp, iname, id, name)
        }.data
    }
}


data class LinkSpec(
    val source: LinkSpecSrc,
    val target: LinkSpecTgt
) {
    companion object {
        fun fromNetwork(map: Map<String, Any>) = object {


            val source: Map<String, Any> by map
            val target: Map<String, Any> by map

            val data = LinkSpec(LinkSpecSrc.fromNetwork(source), LinkSpecTgt.fromNetwork(target))
        }.data
    }
}


data class Link(
    val name: String,
    val active: Boolean,
    val list_name: String,
    val full_spec: LinkSpec
) {
    companion object {
        fun fromNetwork(map: Map<String, Any>) = object {
            val name: String by map
            val active: Boolean by map
            val list_name: String by map
            val full_spec: Map<String, Any> by map

            val data = Link(name, active, list_name, LinkSpec.fromNetwork(full_spec))
        }.data
    }
}


class LinkSrc(
    val list_name: String,
    val ttl: String,
    val id: String,
    val cls: String
) {
    companion object {
        fun fromNetwork(map: Map<String, String>) = object {
            val list_name: String by map
            val ttl: String by map
            val id: String by map
            val cls: String by map

            val data = LinkSrc(list_name, ttl, id, cls)
        }.data
    }
}


class LinkSink(
    val list_name: String,
    val grp: String,
    val iname: String,
    val id: String,
    val name: String
) {
    companion object {
        fun fromNetwork(map: Map<String, String>) = object {
            val list_name: String by map
            val grp: String by map
            val iname: String by map
            val id: String by map
            val name: String by map

            val data = LinkSink(list_name, grp, iname, id, name)
        }.data
    }
}