package io.gmp.does.lambent.droid

import java.time.LocalDate

data class Machine(
    val name: String,
    val running: RunningEnum,
    val id: String,
    val desc: String,

    val speed: TickEnum = TickEnum.ONES,
    val iname: String?
)

data class Device(
    val iname: String,
    val id: String,
    val name: String,
    val bpp: BPP = BPP.RGB,
    val updated: LocalDate
)

data class LinkSpecSrc(
    val listname: String,
    val ttl: String,
    val id: String,
    val cls: String
)

data class LinkSpecTgt(
    val listname: String,
    val grp: String,
    val iname: String,
    val id: String,
    val name: String
)

data class LinkSpec(
    val source: LinkSpecSrc,
    val target: LinkSpecTgt
)


data class Link(
    val name: String,
    val active: Boolean,
    val list_name: String,
    val full_spec: LinkSpec
)


class LinkSrc(
    listname: String,
    ttl: String,
    id: String,
    cls: String
)


class LinkSink(
    listname: String,
    grp: String,
    iname: String,
    id: String,
    name: String
)