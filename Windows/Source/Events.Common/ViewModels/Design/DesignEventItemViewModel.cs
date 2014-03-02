using System;
using PocketCampus.Events.Models;

namespace PocketCampus.Events.ViewModels.Design
{
    public sealed class DesignEventItemViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }

        public bool IsFavorite { get { return false; } }

        public EventItem Item
        {
            get
            {
                return new EventItem
                {
                    Name = @"Finite-temperature properties of Ba(Zr,Ti)O3 relaxors from first principles",
                    SpeakerName = @"Laurent Bellaiche, University of Arkansas, USA",
                    Location = "MXF1",
                    StartDate = new DateTime( 2014, 3, 24, 13, 15, 0 ),
                    EndDate = new DateTime( 2014, 3, 24, 0, 0, 0 ),
                    IsFullDay = false,
                    TimeOverride = null,
                    ShortDetails = null,
                    PictureUrl = null,
                    PictureThumbnailUrl = @"http://memento.epfl.ch/image/2746/100x100.jpg",
                    HidePictureThumbnail = false,
                    HideName = false,
                    HideInformation = false,
                    Details = @"Relaxor ferroelectrics are characterized by some striking anomalous properties (see, e.g., Refs [1-21] and references&nbsp; therein).&nbsp;&nbsp;&nbsp; For&nbsp; instance,&nbsp; they&nbsp; adopt&nbsp; a&nbsp; peak&nbsp; in&nbsp; their&nbsp; ac&nbsp; dielectric&nbsp; response-versus-temperature function while they remain macroscopically paraelectric and cubic down to the lowest temperatures [1]. Furthermore, this dielectric response deviates from the ``traditional'' Curie-Weiss law [22] for temperatures lower than the so-called Burns temperature [2]. Other examples of anomalous properties include the plateau observed&nbsp; in&nbsp; their&nbsp; static,&nbsp; dc&nbsp; dielectric&nbsp; response&nbsp; at&nbsp; low&nbsp; temperature&nbsp; [23,24],&nbsp; and&nbsp; the&nbsp; unusual&nbsp; temperature behavior [16] of the Edwards-Anderson parameter [25]. Determining the origin of these intriguing effects has been a challenge to scientists for more than half a century. Moreover, many other questions remain opened for discussion.&nbsp; Examples&nbsp; of&nbsp; such&nbsp; questions&nbsp; are:&nbsp; what&nbsp; do&nbsp; the&nbsp; different&nbsp; critical&nbsp; temperatures&nbsp; usually&nbsp; found&nbsp; in relaxors&nbsp; correspond&nbsp; to?&nbsp; Do&nbsp; polar&nbsp; nanoregions&nbsp; really&nbsp; exist&nbsp; in&nbsp; relaxors?&nbsp; If&nbsp; yes,&nbsp; do&nbsp; they&nbsp; only&nbsp; form&nbsp; inside chemically-ordered regions? Is it necessary that antiferroelectricity develops in order for the relaxor behavior to occur? Are random fields and random strains really the mechanisms responsible for relaxor behavior? If not, what are these mechanisms?
<br />
<br /> Motivated to resolve such questions and to better understand relaxors, we decided to study disordered Ba(Zr0.5Ti0.5)O3&nbsp; (BZT) solid solutions, via the development and use of a first-principles-based effective Hamiltonian. Note that BZT is also fascinating because, in addition to be a relaxor within some compositional range, its parent compounds are rather different, namely BaZrO3 is paraelectric while BaTiO3 is a typical ferroelectric.
<br />
<br /> Interestingly,&nbsp; our ab-initio-based&nbsp; calculations&nbsp; not only reproduce&nbsp; the anomalous&nbsp; features of relaxors but also offer a deep microscopic insight into BZT [26,27,28]. Such insight allows to successfully answer the aforementioned questions, and will be discussed in detail during this talk.
<br />
<br /> This work is mostly supported by ONR Grants N00014-11-1-0384, N00014-12-1-1034 and N00014-08-1-
<br /> 0915. We also acknowledge the ARO grant W911NF-12-1-0085, NSF grant DMR-1066158, and Department of&nbsp; Energy,&nbsp; Office&nbsp; of&nbsp; Basic&nbsp; Energy&nbsp; Sciences,&nbsp; under&nbsp; contract&nbsp; ER-46612&nbsp; for&nbsp; discussions&nbsp; with&nbsp; scientists sponsored by these grants. Some computations&nbsp; were also made possible thanks to the MRI grant 0722625 from&nbsp; NSF,&nbsp; the&nbsp; ONR&nbsp; grant&nbsp; N00014-07-1-0825&nbsp; (DURIP)&nbsp; and&nbsp; a&nbsp; Challenge&nbsp; grant&nbsp; from&nbsp; the&nbsp; Department&nbsp; of Defense.
<br />
<br /> References:
<br /> [1] Cross, L.E., Ferroelectrics 151, 305 (1994).
<br /> [2] Burns, G.&nbsp; and Dacol, F.H., Phys. Rev. B 28, 2527 (1983).
<br /> [3] Smolensky, G. A. et al. Ferroelectrics and Related Materials (Gordon and Breach, New York, 1981). [4] Westphal V., Kleemann, W. and Glinchuk, M.D, Phys. Rev. Lett. 68, 847 (1992).
<br /> [5] Tagantsev A.K. and Glazounov, E.Z., Phys. Rev. B 57, 18 (1998). [6] Pirc, R. and Blinc, R., Phys. Rev. B 60, 13470 (1999).
<br /> [7] Jeong, I.-K. et al. Phys. Rev. Lett. 94, 147602 (2005).
<br /> [8] Bai, Y. and Jin, L., J. Phys. D: Appl. Phys. 41, 152008 (2008). [9] Vogel, H., Phys. Z. 22, 645 (1921).
<br /> [10] Fulcher, G. S., J. Am. Ceram. Soc. 8, 339 (1925).
<br /> [11] Dkhil, B. et al, Phys. Rev. B 80, 064103 (2009).
<br /> [12] Svitelskiy, O. et al, Phys. Rev. B 72, 172106 (2005).
<br /> [13] Tinte, S., Burton, B. P., Cockayne, E. and Waghmare U., Phys. Rev. Lett. 97, 137601 (2006). [14] Ishchuk, V.M., Baumer, V. N. and&nbsp; Sobolev, V. L., J. Phys.: Condens. Matter 17, L177 (2005). [15] Takesue, N, Fujii, Y., Ichihara, M. and Chen, H., Phys. Rev. Lett. 82, 3709 (1999).
<br /> [16] Blinc, R. et al, Phys. Rev. B 63, 024104 (2000).
<br /> [17] Vugmeister, B. E. and Rabitz H., Phys. Rev. B 57, 7581 (1998).
<br /> [18] Viehland, D., Jang, S.J., Cross, L.E. and Wuttig, M., J. Appl. Phys. 68, 2916 (1990).
<br /> [19] Colla, E.V., Koroleva, E. Y., Okuneva, N.M. and Vakhrushev, S.B., Phys. Rev. Lett. 74, 1681 (1995). [20] Grinberg, I., Juhas, P., Davies, P. K. and Rappe, A. M., Phys. Rev. Lett. 99, 267603 (2007).
<br /> [21] Al-Zein, A., Hlinka, J., Rouquette, J. and Hehlen, B., Phys Rev Lett. 105, 017601 (2010). [22] Kittel, C. Introduction to Solid State Physics 7th ed. (1996).
<br /> [23] Kutnjak, Z. et al, Phys. Rev. B 59, 294 (1999).
<br /> [24] Levstik, A., Kutnjak, Z., Filipic, C. and Pirc, R., Phys. Rev. B 57, 11204 (1998). [25] Edwards, S. F. and Anderson, P. W., J. Phys. F 5, 965 (1975).
<br /> [26] A. R. Akbarzadeh,&nbsp; S. Prosandeev,&nbsp; E. J. Walter, A. Al-Barakaty&nbsp; and L. Bellaiche,&nbsp; Phys. Rev. Lett. 108,
<br /> 257601 (2012).
<br /> [27] S. Prosandeev,&nbsp; D. Wang, A. R. Akbarzadeh,&nbsp; B. Dkhil and L. Bellaiche,&nbsp; Phys. Rev. Lett., 110, 207601 (2013).
<br /> [28] S. Prosandeev, D. Wang and L. Bellaiche, Phys. Rev. Lett., 111, 247602 (2013).
<br />
<br /> Bio: Professor Bellaiche earned his doctorate from the University of Paris in 1994. From 1994 to 1995 he was a teaching and research associate at the University of Paris, which he left to join the National Renewable Energy Laboratory in Colorado as a post-doctoral fellow. Before coming to the university, he worked as a research associate at Rutgers University in New Jersey.
<br />
<br /> His primary research interests are to reveal the properties of ferroelectric systems at the nanoscale level, in general, and to understand how and why they differ from the corresponding bulks, in particular. He thinks such research can lead to smart cards with higher storage, ultrasound machines with sharper resolutions and sonar-listening devices that can scan greater distances.",
                    DetailsUrl = @"http://www.uark.edu/misc/aaron5/index.html"
                };
            }
        }
#endif
    }
}