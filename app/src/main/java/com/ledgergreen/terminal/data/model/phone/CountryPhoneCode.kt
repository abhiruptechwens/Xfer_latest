package com.ledgergreen.terminal.data.model.phone

data class CountryPhoneCode(
    val countryCode: String,
    val countryName: String,
    val phoneCode: Int,
) {
    fun isUSCode() = countryCode == "us" || countryCode == "ca"
}

object CountryCodes {
    val defaultUSPhoneCode = CountryPhoneCode("us", "United States", 1)

    val all: List<CountryPhoneCode> = listOf(
        CountryPhoneCode("us", "United States", 1),
        CountryPhoneCode("ca", "Canada", 1),
        CountryPhoneCode("mx", "Mexico", 52),
        CountryPhoneCode("af", "Afghanistan", 93),
        CountryPhoneCode("al", "Albania", 355),
        CountryPhoneCode("dz", "Algeria", 213),
        CountryPhoneCode("as", "American Samoa", 1684),
        CountryPhoneCode("ad", "Andorra", 376),
        CountryPhoneCode("ao", "Angola", 244),
        CountryPhoneCode("ai", "Anguilla", 1264),
        CountryPhoneCode("ag", "Antigua and Barbuda", 1268),
        CountryPhoneCode("ar", "Argentina", 54),
        CountryPhoneCode("am", "Armenia", 374),
        CountryPhoneCode("aw", "Aruba", 297),
        CountryPhoneCode("au", "Australia", 61),
        CountryPhoneCode("at", "Austria", 43),
        CountryPhoneCode("az", "Azerbaijan", 994),
        CountryPhoneCode("bs", "Bahamas", 1242),
        CountryPhoneCode("bh", "Bahrain", 973),
        CountryPhoneCode("bd", "Bangladesh", 880),
        CountryPhoneCode("bb", "Barbados", 1246),
        CountryPhoneCode("by", "Belarus", 375),
        CountryPhoneCode("be", "Belgium", 32),
        CountryPhoneCode("bz", "Belize", 501),
        CountryPhoneCode("bj", "Benin", 229),
        CountryPhoneCode("bm", "Bermuda", 1441),
        CountryPhoneCode("bt", "Bhutan", 975),
        CountryPhoneCode("bo", "Bolivia", 591),
        CountryPhoneCode("ba", "Bosnia and Herzegovina", 387),
        CountryPhoneCode("bw", "Botswana", 267),
        CountryPhoneCode("br", "Brazil", 55),
        CountryPhoneCode("io", "British Indian Ocean Territory", 246),
        CountryPhoneCode("vg", "British Virgin Islands", 1284),
        CountryPhoneCode("bn", "Brunei", 673),
        CountryPhoneCode("bg", "Bulgaria", 359),
        CountryPhoneCode("bf", "Burkina Faso", 226),
        CountryPhoneCode("bi", "Burundi", 257),
        CountryPhoneCode("kh", "Cambodia", 855),
        CountryPhoneCode("cm", "Cameroon", 237),
        CountryPhoneCode("cv", "Cape Verde", 238),
        CountryPhoneCode("bq", "Caribbean Netherlands", 599),
        CountryPhoneCode("ky", "Cayman Islands", 1345),
        CountryPhoneCode("cf", "Central African Republic", 236),
        CountryPhoneCode("td", "Chad", 235),
        CountryPhoneCode("cl", "Chile", 56),
        CountryPhoneCode("cn", "China", 86),
        CountryPhoneCode("cx", "Christmas Island", 61),
        CountryPhoneCode("cc", "Cocos (Keeling) Islands", 61),
        CountryPhoneCode("co", "Colombia", 57),
        CountryPhoneCode("km", "Comoros", 269),
        CountryPhoneCode("cd", "Congo (DRC)", 243),
        CountryPhoneCode("cg", "Congo (Republic) (Congo-Brazzaville)", 242),
        CountryPhoneCode("ck", "Cook Islands", 682),
        CountryPhoneCode("cr", "Costa Rica", 506),
        CountryPhoneCode("ci", "Côte d’Ivoire", 225),
        CountryPhoneCode("hr", "Croatia", 385),
        CountryPhoneCode("cu", "Cuba", 53),
        CountryPhoneCode("cw", "Curaçao", 599),
        CountryPhoneCode("cy", "Cyprus", 357),
        CountryPhoneCode("cz", "Czech Republic", 420),
        CountryPhoneCode("dk", "Denmark", 45),
        CountryPhoneCode("dj", "Djibouti", 253),
        CountryPhoneCode("dm", "Dominica", 1767),
        CountryPhoneCode("do", "Dominican Republic", 1),
        CountryPhoneCode("ec", "Ecuador", 593),
        CountryPhoneCode("eg", "Egypt", 20),
        CountryPhoneCode("sv", "El Salvador", 503),
        CountryPhoneCode("gq", "Equatorial Guinea", 240),
        CountryPhoneCode("er", "Eritrea", 291),
        CountryPhoneCode("ee", "Estonia", 372),
        CountryPhoneCode("et", "Ethiopia", 251),
        CountryPhoneCode("fk", "Falkland Islands", 500),
        CountryPhoneCode("fo", "Faroe Islands", 298),
        CountryPhoneCode("fj", "Fiji", 679),
        CountryPhoneCode("fi", "Finland", 358),
        CountryPhoneCode("fr", "France", 33),
        CountryPhoneCode("gf", "French Guiana", 594),
        CountryPhoneCode("pf", "French Polynesia", 689),
        CountryPhoneCode("ga", "Gabon", 241),
        CountryPhoneCode("gm", "Gambia", 220),
        CountryPhoneCode("ge", "Georgia", 995),
        CountryPhoneCode("de", "Germany", 49),
        CountryPhoneCode("gh", "Ghana", 233),
        CountryPhoneCode("gi", "Gibraltar", 350),
        CountryPhoneCode("gr", "Greece", 30),
        CountryPhoneCode("gl", "Greenland", 299),
        CountryPhoneCode("gd", "Grenada", 1473),
        CountryPhoneCode("gp", "Guadeloupe", 590),
        CountryPhoneCode("gu", "Guam", 1671),
        CountryPhoneCode("gt", "Guatemala", 502),
        CountryPhoneCode("gg", "Guernsey", 44),
        CountryPhoneCode("gn", "Guinea", 224),
        CountryPhoneCode("gw", "Guinea-Bissau", 245),
        CountryPhoneCode("gy", "Guyana", 592),
        CountryPhoneCode("ht", "Haiti", 509),
        CountryPhoneCode("hn", "Honduras", 504),
        CountryPhoneCode("hk", "Hong Kong", 852),
        CountryPhoneCode("hu", "Hungary", 36),
        CountryPhoneCode("is", "Iceland", 354),
        CountryPhoneCode("in", "India", 91),
        CountryPhoneCode("id", "Indonesia", 62),
        CountryPhoneCode("ir", "Iran", 98),
        CountryPhoneCode("iq", "Iraq", 964),
        CountryPhoneCode("ie", "Ireland", 353),
        CountryPhoneCode("im", "Isle of Man", 44),
        CountryPhoneCode("il", "Israel", 972),
        CountryPhoneCode("it", "Italy", 39),
        CountryPhoneCode("jm", "Jamaica", 1876),
        CountryPhoneCode("jp", "Japan", 81),
        CountryPhoneCode("je", "Jersey", 44),
        CountryPhoneCode("jo", "Jordan", 962),
        CountryPhoneCode("kz", "Kazakhstan", 7),
        CountryPhoneCode("ke", "Kenya", 254),
        CountryPhoneCode("ki", "Kiribati", 686),
        CountryPhoneCode("kw", "Kuwait", 965),
        CountryPhoneCode("kg", "Kyrgyzstan", 996),
        CountryPhoneCode("la", "Laos", 856),
        CountryPhoneCode("lv", "Latvia", 371),
        CountryPhoneCode("lb", "Lebanon", 961),
        CountryPhoneCode("ls", "Lesotho", 266),
        CountryPhoneCode("lr", "Liberia", 231),
        CountryPhoneCode("ly", "Libya", 218),
        CountryPhoneCode("li", "Liechtenstein", 423),
        CountryPhoneCode("lt", "Lithuania", 370),
        CountryPhoneCode("lu", "Luxembourg", 352),
        CountryPhoneCode("mo", "Macau", 853),
        CountryPhoneCode("mk", "Macedonia", 389),
        CountryPhoneCode("mg", "Madagascar", 261),
        CountryPhoneCode("mw", "Malawi", 265),
        CountryPhoneCode("my", "Malaysia", 60),
        CountryPhoneCode("mv", "Maldives", 960),
        CountryPhoneCode("ml", "Mali", 223),
        CountryPhoneCode("mt", "Malta", 356),
        CountryPhoneCode("mh", "Marshall Islands", 692),
        CountryPhoneCode("mq", "Martinique", 596),
        CountryPhoneCode("mr", "Mauritania", 222),
        CountryPhoneCode("mu", "Mauritius", 230),
        CountryPhoneCode("yt", "Mayotte", 262),
        CountryPhoneCode("fm", "Micronesia", 691),
        CountryPhoneCode("md", "Moldova", 373),
        CountryPhoneCode("mc", "Monaco", 377),
        CountryPhoneCode("mn", "Mongolia", 976),
        CountryPhoneCode("me", "Montenegro", 382),
        CountryPhoneCode("ms", "Montserrat", 1664),
        CountryPhoneCode("ma", "Morocco", 212),
        CountryPhoneCode("mz", "Mozambique", 258),
        CountryPhoneCode("mm", "Myanmar", 95),
        CountryPhoneCode("na", "Namibia", 264),
        CountryPhoneCode("nr", "Nauru", 674),
        CountryPhoneCode("np", "Nepal", 977),
        CountryPhoneCode("nl", "Netherlands", 31),
        CountryPhoneCode("nc", "New Caledonia", 687),
        CountryPhoneCode("nz", "New Zealand", 64),
        CountryPhoneCode("ni", "Nicaragua", 505),
        CountryPhoneCode("ne", "Niger", 227),
        CountryPhoneCode("ng", "Nigeria", 234),
        CountryPhoneCode("nu", "Niue", 683),
        CountryPhoneCode("nf", "Norfolk Island", 672),
        CountryPhoneCode("kp", "North Korea", 850),
        CountryPhoneCode("mp", "Northern Mariana Islands", 1670),
        CountryPhoneCode("no", "Norway", 47),
        CountryPhoneCode("om", "Oman", 968),
        CountryPhoneCode("pk", "Pakistan", 92),
        CountryPhoneCode("pw", "Palau", 680),
        CountryPhoneCode("ps", "Palestine", 970),
        CountryPhoneCode("pa", "Panama", 507),
        CountryPhoneCode("pg", "Papua New Guinea", 675),
        CountryPhoneCode("py", "Paraguay", 595),
        CountryPhoneCode("pe", "Peru", 51),
        CountryPhoneCode("ph", "Philippines", 63),
        CountryPhoneCode("pl", "Poland", 48),
        CountryPhoneCode("pt", "Portugal", 351),
        CountryPhoneCode("pr", "Puerto Rico", 1),
        CountryPhoneCode("qa", "Qatar", 974),
        CountryPhoneCode("re", "Réunion", 262),
        CountryPhoneCode("ro", "Romania", 40),
        CountryPhoneCode("ru", "Russia", 7),
        CountryPhoneCode("rw", "Rwanda", 250),
        CountryPhoneCode("bl", "Saint Barthélemy", 590),
        CountryPhoneCode("sh", "Saint Helena", 290),
        CountryPhoneCode("kn", "Saint Kitts and Nevis", 1869),
        CountryPhoneCode("lc", "Saint Lucia", 1758),
        CountryPhoneCode("mf", "Saint Martin", 590),
        CountryPhoneCode("pm", "Saint Pierre and Miquelon", 508),
        CountryPhoneCode("vc", "Saint Vincent and the Grenadines", 1784),
        CountryPhoneCode("ws", "Samoa", 685),
        CountryPhoneCode("sm", "San Marino", 378),
        CountryPhoneCode("st", "São Tomé and Príncipe", 239),
        CountryPhoneCode("sa", "Saudi Arabia", 966),
        CountryPhoneCode("sn", "Senegal", 221),
        CountryPhoneCode("rs", "Serbia", 381),
        CountryPhoneCode("sc", "Seychelles", 248),
        CountryPhoneCode("sl", "Sierra Leone", 232),
        CountryPhoneCode("sg", "Singapore", 65),
        CountryPhoneCode("sx", "Sint Maarten", 1721),
        CountryPhoneCode("sk", "Slovakia", 421),
        CountryPhoneCode("si", "Slovenia", 386),
        CountryPhoneCode("sb", "Solomon Islands", 677),
        CountryPhoneCode("so", "Somalia", 252),
        CountryPhoneCode("za", "South Africa", 27),
        CountryPhoneCode("kr", "South Korea", 82),
        CountryPhoneCode("ss", "South Sudan", 211),
        CountryPhoneCode("es", "Spain", 34),
        CountryPhoneCode("lk", "Sri Lanka", 94),
        CountryPhoneCode("sd", "Sudan", 249),
        CountryPhoneCode("sr", "Suriname", 597),
        CountryPhoneCode("sj", "Svalbard and Jan Mayen", 47),
        CountryPhoneCode("sz", "Swaziland", 268),
        CountryPhoneCode("se", "Sweden", 46),
        CountryPhoneCode("ch", "Switzerland", 41),
        CountryPhoneCode("sy", "Syria", 963),
        CountryPhoneCode("tw", "Taiwan", 886),
        CountryPhoneCode("tj", "Tajikistan", 992),
        CountryPhoneCode("tz", "Tanzania", 255),
        CountryPhoneCode("th", "Thailand", 66),
        CountryPhoneCode("tl", "Timor-Leste", 670),
        CountryPhoneCode("tg", "Togo", 228),
        CountryPhoneCode("tk", "Tokelau", 690),
        CountryPhoneCode("to", "Tonga", 676),
        CountryPhoneCode("tt", "Trinidad and Tobago", 1868),
        CountryPhoneCode("tn", "Tunisia", 216),
        CountryPhoneCode("tr", "Turkey", 90),
        CountryPhoneCode("tm", "Turkmenistan", 993),
        CountryPhoneCode("tc", "Turks and Caicos Islands", 1649),
        CountryPhoneCode("tv", "Tuvalu", 688),
        CountryPhoneCode("vi", "U.S. Virgin Islands", 1340),
        CountryPhoneCode("ug", "Uganda", 256),
        CountryPhoneCode("ua", "Ukraine", 380),
        CountryPhoneCode("ae", "United Arab Emirates", 971),
        CountryPhoneCode("gb", "United Kingdom", 44),
        CountryPhoneCode("uy", "Uruguay", 598),
        CountryPhoneCode("uz", "Uzbekistan", 998),
        CountryPhoneCode("vu", "Vanuatu", 678),
        CountryPhoneCode("va", "Vatican City", 39),
        CountryPhoneCode("ve", "Venezuela", 58),
        CountryPhoneCode("vn", "Vietnam", 84),
        CountryPhoneCode("wf", "Wallis and Futuna", 681),
        CountryPhoneCode("eh", "Western Sahara", 212),
        CountryPhoneCode("ye", "Yemen", 967),
        CountryPhoneCode("zm", "Zambia", 260),
        CountryPhoneCode("zw", "Zimbabwe", 263),
        CountryPhoneCode("ax", "Åland Islands", 358),
    )
}