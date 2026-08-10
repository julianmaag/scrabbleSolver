package com.example.scrabblesolver.repository;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CommonScrabbleWords implements IAvailableWords {

    public CommonScrabbleWords() {}
    public static final String WORDS_RAW = """
        aa ab ad ae ag ah ai al am an ar as at aw ax ay
        ba be bi bo bu by
        da de di do
        ea ed ef eh el em en er es et ew ex
        fa fe fi
        ga gi go
        ha he hi hm ho
        id if in is it
        ja jo
        ka ki
        la li lo
        ma me mi mm mo mu my
        na ne no nu
        ob od oe of oh oi ok om on oo op or os ow ox oy
        pa pe pi po
        qi
        re
        sh si so
        ta te ti to
        uh um un up ur us ut
        we wo
        xi xu
        ya ye yo
        za
        aah aal aas aba abo abs aby ace ach ack act add ado ads adz aff aft aga age ago ags aha ahi ahs aid aim ain air ais ait aka ake ala alb ale alf alp alt ama ami amp amu ana and ane ani ant any ape apt arb arc are ark arm art ash ask asp ass ate atm att auk ava ave avo aw awe axe aye
        baa bad bag bah ban bar bat bay bed beg bet bib bid big bin bit bog bon boo bop bot bow box boy bra bub bud bug bun bur bus but buy
        cab cad cam can cap car cat caw cob cod cog col con cop cor cot cow coy cub cup cur cut
        dab dad dag dah dam dap day deb den dew did dig dim din dip dis dit doc doe dog dol don dop dot dow dry dub dud due dug dun duo
        ear eat eel egg ego elf elk elm emu end era err eve ewe eye
        fad fag fan far fat fax fay fed fen few fez fib fid fig fin fir fit fix fly fob foe fog fon fop for fox foy fud fug fun fur
        gab gad gag gal gap gas gay gee gel gem get gig gin gnu gob god got grr gum gun gut guy
        had hag hah ham hap has hat haw hay hen her hew hey hid him hip his hit hob hod hoe hog hop hot how hoy hub hue hug huh hum hun hup hyp
        ice icy ids ilk ill imp ink inn ion ire irk
        jab jag jam jar jaw jay jet jib jig job jog jot jow joy jug jut
        kab kae kaf kas kat kea keg ken kex key kid kin kit
        lab lac lad lag lam lap law lax lay lea led leg let lev lid lip lit lob log lop lot low lug
        mad man map mar mat maw max may men met mew mid mil mob mod mop mow mud mug mun mut
        nab nae nag nah nap nay neb nee net new nil nip nit nob nod nog nor not now nub nun nut
        oak oaf oak oar oat ode off oft ohm oho oil old one opt orb ore our out ova owe owl own
        pad pal pan pap par pat paw pay pea pee peg pen pep per pet pew phi pie pig pin pip pit ply pod poi pol pop pot pow pox pro pry pub pud pug pun pup pus put
        rah raj ram ran rap rat raw ray red ref rep rev rim rip rob rod roe rot row rub rum run rut rye
        sac sad sag sap sat saw say sea set sew ski sky sly sob sod son sop sot sow soy spa spy Sri sty sub sue sum sun sup
        tab tad tan tap tar tat tax tea ten the thy tic tin tip toe tom too top tot tow toy try tub tug tun two
        ugh ulm ump urb use
        van var vat vaw via vie vim vug vum
        wab wad wag wan war was wax way web wed wig win wit woe wog wok won woo wop wot wow
        yak yam yap yaw yep yes yet yew yid yin yip yob yod yok you yow
        zap zed zen zig zip zit zoo
        aahed abby ache acid acme acne acre acts aged ages agog ague aide akin aloe also alto amok amour amps amuse ante anti ants apex apps apse arch area aria arid arow arty ashy atop auks auto avid avow away awed awes awed awry axed axes axle
        baby back bade bail bait bake bale balk ball balm band bane bang bank bare bark barn base bash bask bass bate bath baud bawl bead beak beam bean bear beat beck beef been beer beet beguile bell belt bend bent berg best bide bier bile bilk bill bind bite blot blow blue blur boar bode body bold bole bolt bond bone book boom boon boot bore born boss both bough bout bowl brag bram brat bred brew brig brim brow bubs buck buff bulk bull bump bunk buoy burn burp burr bury bush bust butt buzz
        cage cake call calm came camp cane cape card care carl carp cart case cash cask cast cave caws cede cell chad chap char chat chef chew chin chip chop chow cite clam clap claw clay clef clew clip clog clop clot club clue coal coat coax coil coke cold cole colt come cone cons cook cool cope cope cord core cork corn cost coup cove cozy crab crew crib crop crow crud cube cued cuff cull cult cure curl dace
        dace dago dale damp dare dark darn dart dash dato daub dawn days daze dead deaf deal dean dear deed deep deer deft deja deli dell demo dent desk dhal dhal dice died dike dill dine ding dire disk diva dive dock dole doll dome done doom door dote dove down doze draw drew drip drop drug drum dual dude duel duke dull dune dunk dusk dust dyer
        each earl earn ease east edge egos eked elan else emit emote epic even ever evil exam exam exes expo eyen
        face fact fade fail fair fake fall fame fang farm fast fate faun faze fead feat feed feel feet fell felt fend fern fife file fill film find fine fink fire firm fish fist five flag flan flap flat flaw flaw fled flew flit floe flog flow flue foam fobs foil folk fond font food fool ford fore fork form fort foul four fowl fray free fret from froe froe fume fund funk fury fuse fuss
        gala gale gall game gang garb gawk gaze geld gelt gene gent gibe gild gill gilt girl give glad glee glen glib glop glow glut gnat goal goat goes gold golf gong good goon gore gown grab grad gram gray grew grid grim grin grip gist gust guys
        haet hale half hall halt hame hang hard hare harm harp hash hash hate haul have hawk haze head heal heap hear heat heel helm hems herb here hero hest hewn hide high hill hilt hind hint hoar hobo hold hole home hone hood hoof hook hoop hope horn host hour hove howl hull hulk hump hung hunk hunt hurt husk hymn
        idea idle idol inch inch inks inky into iron isle itch
        jack jail jake jape jato java jeer jell jerk jinx john join joist joke jolt joss jouk jowl jump junk just
        keen keep kelp kept kern kick kill kiln kind king kiss knob knot know kola
        lace lack lade laid lain lake lame lamp land lane lank lard lark lash lass last late lath laud lawn laze lazy lead leaf leak lean leap left lend lens lese lest levy lick lief lilt lime limp linden line link lint lira lire lisp list live load loam loan lock lode loft lone long look loom loon loop lore lorn lose loss lout love luff lull lune lung lure lurk lust lute lynx lyre
        mace made mail main mane many mare mark mask mast mate maze meal mean meat meet meld melt memo mend mere mesa mesh meet mice mike mild mile milk mill mime mine mint mire mire miss mist moat mode mold mole molt monk mood moor moot more moth move moue muck mule murk muse musk must mutt
        nada nail name nape nard nave neat neck need nerd nest news next nick nine norm nose note nude null
        oafs oath oboe odds omen omit once only onto ooze open opus ores orgy oval oven over ovum oxen
        pace pack page pain pair pale palm pare park part pass past pave pawn peel peer pelt peon perk pest pick pier pike pile pill pine pipe pike plan play plea plod plot plow plum plus poem poet pole poll polo pond pong pool poor pore pose posh post pour pout pray prep prim prod prop prowl puck pull pulp pump punt purl push
        quay quit quiz
        race rack rage raid rail rain rake ramp rang rank rant rape rash rasp rate rave raw razz read real reap reel rely rend rent rest rice rich ride rife rife rift ring riot ripe rise risk roam roar robe rock rode role roll romp roof root rope rose rosy rote rout rove rowdy rude rule rune rung rush ruse rust
        sack saga sage said sail sake sale salt same sand sane sang sank sash save sawn scab scam scan scar scat scow seal seam sear seed seek seem seen seep self send sent shed shim shin ship shod shoe shoo shop shot show shun shut sick sift sign silk sill sing sink sire site size skid skim skip skull slam slap slat sled slim slip slit slob sloe slop slot slow slue slum slur smew smut snap snag snit snob snod snug soak soap soar sock sofa soil sole some song soon sore sort soul soup sour sown spam span spar spin spit spot spud spun stab stag star stay stem step stew stir stop stow stub stun such suit sulk sung sunk sure swam swan swap swat sway swim swum sync
        tace tack take tale tall tame tang tank tare tart task taxi teak teal team tear teem teen tell tend tent test text than that them then they thin this thou tide tied tier till time tine tiny tire toad toil toll tomb tone tong took tool toot tore torn toss tour toward town trace tram trap tray trek trim trio trip trod true truk tuck tule tuft tune turf turn twig twin tyke type
        ugly upon used user
        vain vale van vane vary vase vale vale veal vein veld vent verb very vial view vile vine void volt vote vow
        wade wail wait wake wane warp wart wary wash wast wave wean wear weed week weft well welt wend were west wick wide wile will wilt wind wine wing wink wire wise wish wisp wist with woke wolf womb wood wool word wore work worm wort wove wren writ
        yawl year yell your
        zeal zest zinc zone zoom
        """.trim();

    public static final Set<String> WORD_SET = Arrays.stream(WORDS_RAW.split("\\s+"))
            .map(String::toLowerCase)
            .filter(w -> w.length() >= 2)
            .collect(Collectors.toUnmodifiableSet());

    public Set<String> getAvailableWords(){
        return WORD_SET;
    }

}
