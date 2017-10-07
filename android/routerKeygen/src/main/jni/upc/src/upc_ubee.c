/*
 * RE by ph4r05, miroc
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <md5.h>
#include "upc_ubee.h"

// Result of a passphrase generator is a password of 8 characters using classical english alphabet, uppercase.
// libUtility.so contains database of profanities. If any of word in this array happens to occur as a substring
// in the computed passphrase, new passphrase is generated, now using alphabet without vowels to avoid another profanity.
#define UBEE_NONINSULTING_ALPHABET "BBCDFFGHJJKLMNPQRSTVVWXYZZ"
// Simple macro to get size of profanities array
#define PROFANITY_COUNT (sizeof(profanities)/sizeof(profanities[0]))
// All profanities found in the source binary, alphabetically sorted, converted to upper case.
// Address in the original binary is 0x00040D74.
const char * profanities[] = {
              "ABBO",       "ABUSE",      "ACOCK",      "AGGRO",      "AIDS",       "ANAL",       "ANNAL",      "ANNAS",      "ARSES",
              "ARSIS",      "ASS",        "ASSAI",      "ASSAY",      "ASSES",      "ASSET",      "BABES",      "BALL",       "BALLS",
              "BALLY",      "BANAL",      "BANGS",      "BARFS",      "BARMY",      "BASTARD",    "BAWDS",      "BAWDY",      "BAWLS",
              "BEERS",      "BELCH",      "BIGOT",      "BIMBO",      "BINGE",      "BITCH",      "BLONDE",     "BLOOD",      "BLOW",
              "BLOWN",      "BLOWS",      "BLOWY",      "BOFFS",      "BOGAN",      "BOLES",      "BOLLS",      "BONDAGE",    "BONED",
              "BONER",      "BONGS",      "BONKS",      "BOOBS",      "BOOBY",      "BOOTY",      "BOOZE",      "BOOZY",      "BOWEL",
              "BOYS",       "BOZOS",      "BRATS",      "BROTHEL",    "BUSHY",      "BUSTS",      "BUSTY",      "BUTCH",      "BUTT",
              "BUTTE",      "BUTTS",      "BUTTY",      "BUXOM",      "CANAL",      "CARNY",      "CECUM",      "CHEST",      "CHICS",
              "CHINK",      "CHOAD",      "CHOTT",      "CHOWS",      "CHUBS",      "CHUCK",      "CHUFA",      "CHURR",      "CLITS",
              "COCCI",      "COCK",       "COCKS",      "COCKY",      "COCOS",      "COKED",      "COKES",      "COOFS",      "COON",
              "COONS",      "CRABS",      "CRACK",      "CRAP",       "CRAPS",      "CROZE",      "CRUCK",      "CRUDE",      "CRUDS",
              "CUM",        "CUMIN",      "CUNT",       "CUNTS",      "CUPEL",      "CURNS",      "CURST",      "CURVY",      "CUTIE",
              "DAGOS",      "DANDY",      "DARKY",      "DEMON",      "DESEX",      "DEVIL",      "DICK",       "DICKS",      "DICKY",
              "DIKED",      "DIKER",      "DIKES",      "DIKEY",      "DILDO",      "DIRT",       "DIRTY",      "DITCH",      "DODGE",
              "DODGY",      "DOGGY",      "DONGA",      "DONGS",      "DOPE",       "DOPED",      "DOPER",      "DORKS",      "DORKY",
              "DRAPE",      "DRUBS",      "DRUGS",      "DRUNK",      "DRUPE",      "DRUSE",      "DUMB",       "DWARF",      "DWEEB",
              "DYKED",      "DYKES",      "DYKEY",      "DYNES",      "EBONY",      "ENEMA",      "ERECT",      "EVILS",      "FADOS",
              "FAERY",      "FAG",        "FAGOT",      "FAIRY",      "FANNY",      "FANON",      "FARDS",      "FARTS",      "FATSO",
              "FATTY",      "FATWA",      "FAUGH",      "FECAL",      "FECES",      "FECKS",      "FEELS",      "FEEZE",      "FELCH",
              "FETAL",      "FETAS",      "FILCH",      "FILTH",      "FISHY",      "FISTS",      "FITCH",      "FITLY",      "FLAPS",
              "FLESH",      "FLEWS",      "FLEYS",      "FLOGS",      "FLONG",      "FORKS",      "FORKY",      "FORME",      "FREAK",
              "FRIGS",      "FRUMP",      "FUCK",       "FUCKS",      "FUCUS",      "FUDGE",      "FUGGY",      "FUSTY",      "FUZEE",
              "FUZES",      "FUZZY",      "FYKES",      "FYTTE",      "GAILY",      "GANJA",      "GAPED",      "GAPER",      "GAPES",
              "GAPPY",      "GASTS",      "GEEKS",      "GIMP",       "GIRLS",      "GIRLY",      "GIVER",      "GIZED",      "GONAD",
              "GOOEY",      "GOOFS",      "GOOFY",      "GOOKS",      "GOONS",      "GOOPS",      "GOOPY",      "GRAPE",      "GROAT",
              "GROGS",      "GROIN",      "GROPE",      "GUANO",      "HADAL",      "HADED",      "HADES",      "HADJI",      "HADST",
              "HAEMS",      "HAETS",      "HAIRY",      "HAREM",      "HATE",       "HEAD",       "HEMES",      "HEMPS",      "HEMPY",
              "HERPES",     "HOBOS",      "HOKED",      "HOKES",      "HOKEY",      "HOKKU",      "HOKUM",      "HOLE",       "HOMER",
              "HOMES",      "HOMEY",      "HOMOS",      "HONKY",      "HOOCH",      "HOOKA",      "HORNY",      "HUMPH",      "HUMPS",
              "HUMPY",      "HUSSY",      "HUTCH",      "HUZZA",      "HYING",      "HYMEN",      "HYPOS",      "IDIOT",      "ITCHY",
              "JAIL",       "JERKS",      "JERKY",      "JOCKS",      "JOINT",      "JORAM",      "JORUM",      "JOTAS",      "JOUAL",
              "JOUKS",      "JUDAS",      "JUGUM",      "KIKES",      "KILIM",      "KINKS",      "KINKY",      "KNOBS",      "KOLOS",
              "KONKS",      "KOOKS",      "KOOKY",      "KOPHS",      "KOPJE",      "KOPPA",      "KOTOS",      "KRAFT",      "LABIA",
              "LABRA",      "LATEX",      "LEERS",      "LEERY",      "LEGGY",      "LEMON",      "LEPTA",      "LETCH",      "LEZZY",
              "LICK",       "LICKS",      "LIDOS",      "LIMEY",      "LOADS",      "LOSER",      "LOVED",      "LOVER",      "LOVES",
              "LOWED",      "LUSTS",      "LUSTY",      "LYSES",      "LYSIN",      "LYSIS",      "LYSSA",      "LYTTA",      "MAARS",
              "MADAM",      "MANIA",      "MANIC",      "MICHE",      "MICKS",      "MICRA",      "MILF",       "MINGE",      "MOANS",
              "MOIST",      "MOLES",      "MOLEST",     "MORON",      "MOUNT",      "MOUTH",      "MUCKS",      "MUCKY",      "MUCOR",
              "MUCRO",      "MUCUS",      "MUFFS",      "NAIVE",      "NAKED",      "NANCY",      "NARCO",      "NARCS",      "NARDS",
              "NARES",      "NARKS",      "NARKY",      "NASAL",      "NASTY",      "NATAL",      "NATCH",      "NATES",      "NERDS",
              "NIGER",      "NOGGS",      "NOHOW",      "NOILS",      "NOSEY",      "NUBIA",      "NUCHA",      "NUDER",      "NUDES",
              "NUDIE",      "NUKED",      "NUKES",      "OBESE",      "OPING",      "OPIUM",      "OVARY",      "PADDY",      "PANSY",
              "PANTS",      "PENIS",      "PERKY",      "PILEI",      "PILES",      "PILIS",      "PILLS",      "PIMP",       "PIMPS",
              "PISS",       "PLUCK",      "PLUGS",      "PLUMP",      "POKED",      "POKER",      "POKES",      "POKEY",      "POLED",
              "POLER",      "POMMY",      "POODS",      "POOFS",      "POOFY",      "POOPS",      "PORGY",      "PORKS",      "PORKY",
              "PORN",       "PORNO",      "PORNS",      "POSED",      "POTTO",      "POTTY",      "POUFS",      "PREST",      "PREXY",
              "PRICK",      "PROSO",      "PROSTITUTE", "PROSY",      "PUBES",      "PUBIC",      "PUBIS",      "PUCKS",      "PUDIC",
              "PUFFS",      "PUFFY",      "PUKED",      "PUKES",      "PUNTO",      "PUNTS",      "PUNTY",      "PUPAE",      "PUSSY",
              "PUTTI",      "PUTTO",      "QUEER",      "QUIFF",      "RABBI",      "RABID",      "RACES",      "RACKS",      "RANDY",
              "RAPED",      "RAPER",      "RAPES",      "RECKS",      "RECTA",      "RECTI",      "RECTO",      "RIGID",      "RIMED",
              "RIMER",      "RIMES",      "ROMPS",      "ROOTS",      "ROOTY",      "ROWDY",      "RUMPS",      "RUTHRUSH",   "SCABS",
              "SCATS",      "SCATT",      "SCORE",      "SCRAG",      "SCREW",      "SCRIM",      "SEAM",       "SEEDY",      "SELVA",
              "SEMEN",      "SEWER",      "SEX",        "SEXED",      "SEXES",      "SEXTS",      "SHAFT",      "SHAGS",      "SHIT",
              "SHITS",      "SICKO",      "SICKS",      "SIRED",      "SIREN",      "SIRES",      "SIRUP",      "SISSY",      "SKIRT",
              "SLITS",      "SLOID",      "SLOPS",      "SLOTS",      "SLOWS",      "SLOYD",      "SLUT",       "SLUTS",      "SLYER",
              "SMACK",      "SMOKE",      "SMOKY",      "SMUT",       "SMUTS",      "SNOGS",      "SNOOD",      "SNOOK",      "SNOOL",
              "SNORT",      "SNOTS",      "SNUFF",      "SOOTH",      "SOOTS",      "SPANK",      "SPERM",      "SPEWS",      "SPICA",
              "SPICE",      "SPICK",      "SPICS",      "SPUNK",      "SQUAW",      "STIFF",      "STINK",      "STOOL",      "STRIP",
              "STUDS",      "SUCK",       "SUCKS",      "SUCRE",      "SUDDS",      "SUDOR",      "SWANG",      "SWANK",      "TARTS",
              "TARTY",      "TESTA",      "TESTS",      "TESTY",      "THIEF",      "THUDS",      "THUGS",      "THUJA",      "TIGHT",
              "TIGON",      "TIKES",      "TIKIS",      "TITS",       "TITTY",      "TUBAS",      "TUBBY",      "TUBED",      "TUCKS",
              "TURD",       "TURDS",      "TWATS",      "UDDER",      "UNDEE",      "UNDIE",      "UNSEX",      "UNZIP",      "UREAL",
              "UREAS",      "UREIC",      "URIAL",      "URINE",      "UVEAL",      "UVEAS",      "UVULA",      "VACUA",      "VAGINA",
              "VAGUS",      "VEINS",      "VEINY",      "VELAR",      "VELDS",      "VOMIT",      "VUGGY",      "VULGO",      "VULVA",
              "WACKS",      "WARTS",      "WEIRD",      "WENCH",      "WETLY",      "WHACK",      "WHOPS",      "WHORE",      "WILLY",
              "WIMPS",      "WIMPY",      "WINED",      "WINES",      "WINEY",      "WIZEN",      "WOADS",      "WODGE",      "WOFUL",
              "WOKEN",      "WOLDS",      "WOMAN",      "WOMBS",      "WOMBY",      "WOMEN",      "WONKS",      "WONKY",      "WOOED",
              "WOOER",      "WOOSH",      "WOOZY",      "YOBBO",      "ZOOID",      "ZOOKS"
};


int ubee_generate_ssid(unsigned const char * mac, unsigned char * ssid, size_t * len)
{
    MD5_CTX ctx;
    unsigned char buff1[100];
	unsigned char buff2[100];
	unsigned char h1[100], h2[100];
	memset(buff1, 0, 100);
	memset(buff2, 0, 100);
	memset(h1, 0, 100);
	memset(h2, 0, 100);

	if (len != NULL && *len < 11){
	    return -1;
	}

	sprintf((char*)buff1, "%2X%2X%2X%2X%2X%2X555043444541554C5453534944", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);

	MD5_Init(&ctx);
	MD5_Update(&ctx, buff1, strlen((char*)buff1) + 1);
	MD5_Final(h1, &ctx);

	sprintf((char*)buff2, "%.02X%.02X%.02X%.02X%.02X%.02X", h1[0]&0xf, h1[1]&0xf, h1[2]&0xf, h1[3]&0xf, h1[4]&0xf, h1[5]&0xf);

	MD5_Init(&ctx);
	MD5_Update(&ctx, buff2, strlen((char*)buff2) + 1);
	MD5_Final(h2, &ctx);

    sprintf((char*)ssid, "UPC%d%d%d%d%d%d%d", h2[0]%10, h2[1]%10, h2[2]%10, h2[3]%10, h2[4]%10, h2[5]%10, h2[6]%10);
    if (len != NULL){
        *len = 10;
    }

    return 1;
}

int ubee_generate_pass(unsigned const char * mac, unsigned char * passwd, size_t * len)
{
    unsigned int i=0,p=0;
    unsigned char hash_buff[100];

    if (len != NULL && *len < 9){
        return -1;
    }

    ubee_generate_pass_raw(mac, hash_buff, passwd);
    for(i=0; i<PROFANITY_COUNT; i++){
        if (strstr(passwd, profanities[i]) != NULL){
            p=1;
            break;
        }
    }

    if (p>0){
        ubee_enerate_profanity_free_pass(hash_buff, passwd);
    }

    if (len != NULL){
        *len=8;
    }

    return 1;
}

int ubee_generate_pass_raw(unsigned const char * mac, unsigned char * hash_buff, unsigned char * passwd)
{
    MD5_CTX ctx;
    unsigned char buff1[100];
    unsigned char buff2[100];
    unsigned char buff3[100];
    unsigned char res[100];
    memset(buff1, 0, 100);
    memset(buff2, 0, 100);
    memset(buff3, 0, 100);
    memset(hash_buff, 0, 100);

    // 1.
    sprintf((char*)buff1, "%2X%2X%2X%2X%2X%2X555043444541554C5450415353504852415345", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);

    // 2.
    MD5_Init(&ctx);
    MD5_Update(&ctx, buff1, strlen((char*)buff1)+1);
    MD5_Final(buff2, &ctx);

    // 3.
    sprintf((char*)buff3, "%.02X%.02X%.02X%.02X%.02X%.02X", buff2[0]&0xF, buff2[1]&0xF, buff2[2]&0xF, buff2[3]&0xF, buff2[4]&0xF, buff2[5]&0xF);

    // 4.
    MD5_Init(&ctx);
    MD5_Update(&ctx, buff3, strlen((char*)buff3)+1);
    MD5_Final(hash_buff, &ctx);

    sprintf((char*)passwd, "%c%c%c%c%c%c%c%c",
            0x41u + ((hash_buff[0]+hash_buff[8]) % 0x1Au),
            0x41u + ((hash_buff[1]+hash_buff[9]) % 0x1Au),
            0x41u + ((hash_buff[2]+hash_buff[10]) % 0x1Au),
            0x41u + ((hash_buff[3]+hash_buff[11]) % 0x1Au),
            0x41u + ((hash_buff[4]+hash_buff[12]) % 0x1Au),
            0x41u + ((hash_buff[5]+hash_buff[13]) % 0x1Au),
            0x41u + ((hash_buff[6]+hash_buff[14]) % 0x1Au),
            0x41u + ((hash_buff[7]+hash_buff[15]) % 0x1Au));

    return 0;
}

int ubee_enerate_profanity_free_pass(unsigned char * hash_buff, unsigned char const * new_pass)
{
    sprintf((char*)new_pass, "%c%c%c%c%c%c%c%c",
            UBEE_NONINSULTING_ALPHABET[((hash_buff[0]+hash_buff[8]) % 0x1Au)],
            UBEE_NONINSULTING_ALPHABET[((hash_buff[1]+hash_buff[9]) % 0x1Au)],
            UBEE_NONINSULTING_ALPHABET[((hash_buff[2]+hash_buff[10]) % 0x1Au)],
            UBEE_NONINSULTING_ALPHABET[((hash_buff[3]+hash_buff[11]) % 0x1Au)],
            UBEE_NONINSULTING_ALPHABET[((hash_buff[4]+hash_buff[12]) % 0x1Au)],
            UBEE_NONINSULTING_ALPHABET[((hash_buff[5]+hash_buff[13]) % 0x1Au)],
            UBEE_NONINSULTING_ALPHABET[((hash_buff[6]+hash_buff[14]) % 0x1Au)],
            UBEE_NONINSULTING_ALPHABET[((hash_buff[7]+hash_buff[15]) % 0x1Au)]);
    return 0;
}

