// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IEventsService

#if DEBUG
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using PocketCampus.Events.Models;

namespace PocketCampus.Events.Services.Design
{
    public sealed class DesignEventsService : IEventsService
    {
        public Task<EventItemResponse> GetEventItemAsync( EventItemRequest request, CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new EventItemResponse
                {
                    Status = EventsStatus.Success,
                    Item = GetEventPoolAsync( null, cancellationToken ).Result.ChildrenItems[13]
                }
            );
        }

        public Task<EventPoolResponse> GetEventPoolAsync( EventPoolRequest request, CancellationToken cancellationToken )
        {
            return Task.FromResult
            (
                new EventPoolResponse
                {
                    Status = EventsStatus.Success,
                    Pool = new EventPool
                    {
                        Id = -1
                    },
                    EventCategories = new Dictionary<int, string>
                    {
                        { 1, "Conferences - Seminars" },
                        { 2, "Thesis defenses" },
                        { 3, "Cultural events" },
                        { 4, "Miscellaneous" },
                        { 5, "Academic calendar" },
                        { 6, "Exhibitions" },
                        { 7, "Inaugural lessons - Lessons of honor" }
                    },
                    EventTags = new Dictionary<string, string>
                    {
                        { "0", "Civil Engineering" },
                        { "1", "Pedagogical Research and Support" },
                        { "2", "Seminars at the Automatic Control Lab" },
                        { "3", "Academic calendar" },
                        { "4", "Computational Biology" },
                        { "5", "IEEE Student Branch" },
                        { "6", "Chair of Geometry" },
                        { "7", "Environmental engineering seminar series" },
                        { "8", "Computational Solid Mechanics Laboratory" },
                        { "9", "Architecture" },
                        { "10", "Data Management & Information Retrieval" },
                        { "11", "Swiss Finance Institute @ EPFL" },
                        { "12", "Physics Section" },
                        { "13", "Swiss Institute for Experimental Cancer Research" },
                        { "14", "Environmental engineering" },
                        { "15", "EDCE Civil and Environmental Engineering" },
                        { "16", "Hydraulic Constructions Laboratory LCH" },
                        { "17", "PolyProg" },
                        { "18", "SV" },
                        { "19", "Agenda LCAV" },
                        { "20", "Sustainable campus" },
                        { "21", "Travel GC" },
                        { "22", "Systems & Networks" },
                        { "23", "Swiss Finance Institute at EPFL" },
                        { "24", "EDIC Candidacy Exams schedule" },
                        { "25", "kis" },
                        { "26", "Brown Bag Seminars in Finance" },
                        { "27", "Brain Mind Institute" },
                        { "28", "LCPM related" },
                        { "29", "ENAC - Ongoing and upcoming field campaigns" },
                        { "30", "IC Colloquium" },
                        { "31", "Maths" },
                        { "32", "STI" },
                        { "33", "BMI Seminars Series" },
                        { "34", "Security & Cryptography" },
                        { "35", "Center for Research In Plasma Physics" },
                        { "36", "Archizoom" },
                        { "37", "Civil engineering seminar series" },
                        { "38", "Digital Humanities Laboratory" },
                        { "39", "Computational Neuroscience Seminars" },
                        { "40", "CDM" },
                        { "41", "SB" },
                        { "42", "Lab Logs" },
                        { "43", "Associations" },
                        { "44", "venturelab" },
                        { "45", "Highschool teachers" },
                        { "46", "CdH" },
                        { "47", "Teaching Support Centre" },
                        { "48", "Architecture & Sciences of the City" },
                        { "49", "LESO LUNCHTIME LECTURES" },
                        { "50", "Graphics & Vision" },
                        { "51", "cultural events" },
                        { "52", "Robopoly" },
                        { "53", "EPFL Start-up" },
                        { "54", "Laboratoire of space studies HD" },
                        { "55", "Flow Cytometry Core Facility" },
                        { "56", "Laboratory of Astrophysics" },
                        { "57", "IC" },
                        { "58", "CHAIR \"GAZ NATUREL\"" },
                        { "59", "Solar Energy and Building Physics Laboratory" },
                        { "60", "Algorithms & Theoretical Computer Science" },
                        { "61", "Swiss Finance Institute @ EPFL" },
                        { "62", "Equal Opportunities Office" },
                        { "63", "Global Health Institute" },
                        { "64", "BMI Seminars" },
                        { "65", "IN SILICO | BUILDING" },
                        { "66", "ENAC" },
                        { "67", "Management of Technology and Entrepreneurship Institute" },
                        { "68", "Computer Architecture & Integrated Systems" },
                        { "69", "Programming Languages & Formal Methods" },
                        { "70", "Section de génie civil" },
                        { "71", "INNOVATION PARK" },
                        { "72", "Steel Structures Laboratory ICOM" },
                        { "73", "Information & Communication Theory" },
                        { "74", "Massive Open Online Courses" },
                        { "75", "Signal & Image Processing" },
                        { "76", "Artificial Intelligence & Machine Learning" },
                        { "77", "LAST EVENTS" },
                        { "78", "Centre for Area and Cultural Studies" },
                        { "79", "CNP Center for Neuroprosthetics" },
                        { "80", "FIFA 13 tournament" },
                        { "81", "Center on Risk Analysis and Risk Governance" },
                        { "82", "study programs promotion" },
                        { "83", "Institute of Bioengineering - IBI" },
                        { "84", "National Centre of Competence in Research Synapsy" },
                        { "85", "Institute of Microengineering Talks" },
                        { "86", "Human-Computer Interaction" },
                        { "87", "Urban Planning" },
                        { "88", "Finance Research Seminars" },
                        { "89", "Institute of Chemical Sciences and Engineering" },
                        { "90", "Technology & Public Policy" },
                        { "91", "Prof. Naef Group" },
                        { "92", "LCPM meetings" },
                        { "93", "EPFL" },
                        { "94", "Laboratory of Soil Mechanics LMS" },
                        { "95", "Unlabeled" }
                    },
                    ChildrenItems = new Dictionary<long, EventItem>
                    {
                        { 0, new EventItem
                        {
                            CategoryId = 1,
                            TagIds = new[] { "0", "1", "2" },
                            Name = @"TBA",
                            SpeakerName = @"Paul Biran, ETH Zürich",
                            Location = "MA A331",
                            StartDate = new DateTime( 2014, 3, 31, 15, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 31, 17, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 1, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"To be announced",
                            SpeakerName = @"Thierry Gacoin, Ecole Polytechnique Paris, France",
                            Location = "MXF1",
                            StartDate = new DateTime( 2014, 3, 31, 13, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 31, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2747/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = @"http://pmc.polytechnique.fr/groupes/chimie/accueil_chimie_fr.html"
                        } },
                        { 2, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"MTEI Seminar by Dr. Evangelos Syrigos, University of Zurich",
                            SpeakerName = @"Dr. Evangelos Syrigos, University of Zurich",
                            Location = "ODY 4.03 - VIP Room",
                            StartDate = new DateTime( 2014, 3, 31, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 31, 13, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2671/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"&quot;Managerial Evaluation of Resource Value – Standalone Value, Complementary Resources, and Market Structure&quot;
<br />
<br />
<strong>Abstract </strong>:
<br />
<br /> Resource value is among the most debated concepts in the resource-based theory. In this paper, we test recent arguments that resource value is a function of a resource’s ex ante standalone value, the presence of complementary resources, and demand-side factors using a dataset of all patents filed by the 33 largest pharmaceutical corporations in the US market during 1980-1985.&nbsp; In line with our predictions, we find a positive direct effect of standalone technological value of a new patented molecule. We further find that the level of demand and the firm’s position in the therapeutic class positively moderate the influence of a patented molecules standalone value on the likelihood of that molecule being further developed through clinical trials. Contrary to our predictions, we find that scientific and technological strength negatively moderate this relationship, a finding that suggests negative complementarity in the form of increased selectiveness in further developing molecules into drugs.",
                            DetailsUrl = null
                        } },
                        { 3, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"No Title",
                            SpeakerName = @"Toni WHITED (University of Rocherster)",
                            Location = "Extranef, room 126",
                            StartDate = new DateTime( 2014, 3, 28, 10, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 28, 12, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 4, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"LCPM Presentation",
                            SpeakerName = @"Aleksandr Pereverzev",
                            Location = "CH G1 495",
                            StartDate = new DateTime( 2014, 3, 28, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 28, 10, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 5, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"An ultrafast camera for chemical imaging",
                            SpeakerName = @"Dr Claire Vallance University of Oxford",
                            Location = "CH G1 495",
                            StartDate = new DateTime( 2014, 3, 27, 16, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 27, 17, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2801/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The development of sensors capable of detecting particles with both high time and high positional resolution is key to improving our understanding in many areas of science.&nbsp; Example application areas range from fundamental scattering studies of chemical reaction mechanisms through to imaging mass spectrometry of surfaces, neutron scattering studies aimed at probing the structure of materials, and time?resolved fluorescence measurements to elucidate the structure and function of biomolecules. In addition to improved throughput resulting from parallelisation of data collection – imaging of multiple different fragments in velocity?map imaging studies, for example - fast image sensors also offer a number of fundamentally new capabilities in areas such as coincidence detection.&nbsp; This talk will review recent developments in fast image sensor technology, with particular reference to the PImMS (Pixel Imaging Mass Spectrometry) sensors developed in Oxford, and will showcase results from a range of different imaging applications.",
                            DetailsUrl = null
                        } },
                        { 6, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Space-time and hp-adaptive discontinuous Galerkin methods for time-domain electromagnetics",
                            SpeakerName = @"Dr. Sascha SCHNEPP (ETH Zürich, Switzerland)",
                            Location = "MA A3 30",
                            StartDate = new DateTime( 2014, 3, 26, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 26, 17, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2689/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Serminar of Numerical Analysis",
                            DetailsUrl = @"http://mathicse.epfl.ch/seminars"
                        } },
                        { 7, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Molecular mechanisms involved in neurodegenerative diseases: a genetic, environmental and biological factors study",
                            SpeakerName = @"Carl JULIEN, Post-doc, Ph.D. Labs of Dr Alex Parker and Pierre Drapeau, Pathology and Cellular Biology, Department of Neurosciences, Université de Montréal, CRCHUM, Montréal, Québec, CANADA",
                            Location = "AI 1153",
                            StartDate = new DateTime( 2014, 3, 26, 14, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 26, 15, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"For many neurodegenerative diseases, environmental and biological factors may interfere with genetic susceptibilities and lead or not to disease manifestation. Here, we found changes in fatty acid profiles from human brain samples of Alzheimer’s disease (AD) and Parkinson’s disease (PD) patients and a decrease of SIRT1 levels in AD brain. We also observed in mouse models of type 1 and type 2 diabetes and in mice anesthetized an aggravation of tau and A? pathologies, the two hallmarks of AD. We also found changes in synaptic markers and in brain fatty acid profiles in mice fed a high-fat diet. Moreover, using genetic models in C. elegans and mouse, we observed involvement of endoplasmic reticulum (ER) stress pathway, the DNA-binding proteins TDP-43 and FUS, and the secreted growth factor progranulin in polyglutamine (polyQ) toxicity found in Huntington’s disease (HD). We also investigated drugs targeting these pathways to identify disease modifiers.",
                            DetailsUrl = null
                        } },
                        { 8, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"BMI Seminar // NeuroTechnologies for the restoration of sensory-motor function in disabled people",
                            SpeakerName = @"Silvestro Micera Translational Neural Engineering Lab, Head Center for Neuroprosthetics and Institute of Bioengineering, School of Engineering, EPFL, Lausanne, Switzerland",
                            Location = "SV1717A",
                            StartDate = new DateTime( 2014, 3, 26, 12, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 26, 13, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/1790/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 9, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"How roots reinforce soil and enhance slope stability",
                            SpeakerName = @"Dr Massimiliano Schwarz, Forest and Food Science. School of Agriculture, Bern University of Applied Sciences CH",
                            Location = "GR A3 32",
                            StartDate = new DateTime( 2014, 3, 25, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 25, 17, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2534/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<u>Abstract</u>:
<br /> The quantification of root reinforcement represents a key issue in different area of engineering (slope stability, soil protection, silviculture/tree stability, hydraulic).&nbsp; Between all the effects of plants on the physical and chemical soil processes (direct and indirect), the mechanical effect of roots is considered particularly important for slope stability. The study of root reinforcement is faced with the high complexity of interactions of processes and factors at different spatial-temporal scales. In particular, the hierarchical spatial heterogeneity of vegetation and its effects on soil processes represents a big challenge for quantitative up-scaling methods. The objective of this seminar is to contextualize the complexity of the root-soil interactions in view of slope stability problems, to review the recent scientific contributions in the quantification of root reinforcement, and to discuss the practical meaning of recent research results.
<br /> The presentation follows the central theme of an up-scaling framework for the implementation of root reinforcement in slope stability analysis, discussing specific topics such as pullout force of single roots, root bundle mechanics, spatial heterogeneity of root distribution, and triggering mechanism of shallow landslides. The above-mentioned factors and processes build up the modules implemented in a numerical model for slope stability calculations, the SOSlope model . The results of simulations performed with the SOSlope model serve as background for the discussion on the role of root reinforcement for protection forests management and bioengineering applications.
<br />
<br />
<u>Short biography</u>:
<br /> With a master thesis on bioengineering at the SLF in Davos, Massimiliano Schwarz graduated in Forest engineering at the University of Florence (IT). After a short experience in the practice in the canton of Grisons (Zuoz, CH), he started a PhD at the WSL on the &quot;Hydro-mechanical characterization of rooted hillslope failure: from field investigations to fiber bundle modeling&quot;, associated to the doctoral schools at both EPFL and ETHZ.
<br /> In 2010, Dr Schwarz started a part-time activity as lecturer and scientific collaborator at the Bern University of Applied Sciences, and as a visiting scientific at the WSL.
<br />
<br /> His professional interest is oriented on different issues related to landscape and resources management in mountain regions. In particular, his research focuses on the quantification of the interaction between vegetation and hydro-mechanical processes at different temporal and spatial scales. His teaching activity at the University of Applied Sciences in Bern covers the topics of bioengineering, natural hazards (landslides, debris flow, and floods), and pedology.",
                            DetailsUrl = null
                        } },
                        { 10, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"A New Perspective on Shear Thickening of Dense Suspensions",
                            SpeakerName = @"Prof. Heinrich M. Jaeger, University of Chicago",
                            Location = "ME B3 31",
                            StartDate = new DateTime( 2014, 3, 25, 14, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 25, 15, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2695/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Dense suspensions of particles in a liquid exhibit a number of counterintuitive, non-Newtonian flow behaviors. Most remarkably, the application of stress can dramatically harden the material, transforming it from a liquid state at rest into a solid-like state when driven strongly.&nbsp; Shear-thickening-based models developed over the last 25 years cannot explain the observed large normal stresses (large enough to support a grown person's weight when running across a pool filled with a suspension such as cornstarch in water). This talk surveys some of the key issues, discusses the stress scales associated with shear thickening in dense suspensions, and outlines a new scenario for impact response.&nbsp; In particular, using high-speed video and x-ray imaging during sudden impact, we are able to link the nonlinear suspension dynamics in a new way to the jamming phase transition.
<br />
<br /> Bio: Heinrich Jaeger is the William J. Friedman and Alicia Townsend Professor of Physics at the University of Chicago. He received his Ph.D. in physics in 1987, under Allen Goldman at the University of Minnesota, working on ultrathin superconducting films.&nbsp; After a postdoc at the University of Chicago 1987-89, Jaeger spent two years at the Centre for Submicron Technology of the University of Delft in The Netherlands. He has been on the faculty at Chicago since 1991, directing the Chicago Materials Research Center from 2001 – 2006, and the James Franck Institute from 2007-2010. Jaeger’s current research focuses on investigations of self-assembled nanoparticle-based structures, on the rheology of dense suspensions, and on studies of the packing and flow of granular materials.",
                            DetailsUrl = null
                        } },
                        { 11, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"La morphogenèse: la construction du vivant",
                            SpeakerName = @"Annick Lesne, mathématicienne, biologiste, directrice de recherche au CNRS, Paris",
                            Location = "Archizoom",
                            StartDate = new DateTime( 2014, 3, 24, 18, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 24, 19, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2615/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Conf&eacute;rence dans le cadre de l'exposition Patrick Berger &agrave; Archizoom",
                            DetailsUrl = @"http://archizoom.epfl.ch/op/edit/annick_lesne"
                        } },
                        { 12, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"TBA",
                            SpeakerName = @"Claude Sabbah, CNRS Strasbourg",
                            Location = "MA A331",
                            StartDate = new DateTime( 2014, 3, 24, 15, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 24, 17, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 13, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Finite-temperature properties of Ba(Zr,Ti)O3 relaxors from first principles",
                            SpeakerName = @"Laurent Bellaiche, University of Arkansas, USA",
                            Location = "MXF1",
                            StartDate = new DateTime( 2014, 3, 24, 13, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 24, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2746/100x100.jpg",
                            HideThumbnail = false,
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
                        } },
                        { 14, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Density-based embedding for multiscale simulations",
                            SpeakerName = @"Christoph Jacob, Tomasz Wesolowski",
                            Location = "BCH 2103",
                            StartDate = new DateTime( 2014, 3, 24, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 27, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2572/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = @"http://www.cecam.org/workshop-965.html"
                        } },
                        { 15, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"No Title",
                            SpeakerName = @"Xavier GIROUD (MIT)",
                            Location = "Extranef, room 126",
                            StartDate = new DateTime( 2014, 3, 21, 10, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 21, 12, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 16, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Discussion meeting",
                            SpeakerName = "",
                            Location = "CH G1 495",
                            StartDate = new DateTime( 2014, 3, 21, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 21, 10, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/1986/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 17, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"EDIC Open House",
                            SpeakerName = "",
                            Location = "BC Atrium",
                            StartDate = new DateTime( 2014, 3, 21, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 22, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The EDIC Open House is a gathering of invited prospective PhD students and the
<a href=""http://ic.epfl.ch/""> IC School</a> community, to learn about our program, the university and the charming Lake Geneva Region.
<br />
<br /> The exact program of this year's edition to be announced.",
                            DetailsUrl = @"http://phd.epfl.ch/edic/openhouse"
                        } },
                        { 18, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Tunneling Control of Chemical Reactions",
                            SpeakerName = @"Prof. Peter Schreiner, Institute of Organic Chemistry, Justus-Liebig Universität Giessen, Germany",
                            Location = "BCH 2218",
                            StartDate = new DateTime( 2014, 3, 20, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 20, 18, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2595/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 19, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Nuclear spin isomers and long-lived spin states: how the right kind of symmetry keeps NMR signals alive",
                            SpeakerName = @"Prof. Malcolm Levitt School of Chemistry University of Southampton",
                            Location = "BCH4310",
                            StartDate = new DateTime( 2014, 3, 20, 16, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 20, 17, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 20, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Synapsy Happy Hour Lausanne - Margot Fournier & Aline Monin",
                            SpeakerName = "",
                            Location = "SV 2.510",
                            StartDate = new DateTime( 2014, 3, 19, 17, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 19, 18, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2714/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<strong>Aline Monin: Prof. Kim Do’s lab</strong>
<br />
<em>Glutathione deficit impairs myelin maturation in experimental models: relevance for white matter integrity in schizophrenia patients.</em>
<br />
<br />
<strong>Margot Fournier: Prof. Kim Do’s lab</strong>
<br />
<em>Extracellular Matrix in Psychosis</em>
<br />
<br /> *An aperitive will follow the talks*
<br />
<br /> Every student and researcher interested in the field of neuroscience and psychiatry is very welcome to join!",
                            DetailsUrl = null
                        } },
                        { 21, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Methane in aquatic systems - Formation, mixing, oxidation, and emission",
                            SpeakerName = @"Dr Carsten Schubert, Surface Waters Research and Management, EAWAG, Duebendorf, CH",
                            Location = "GR A3 32",
                            StartDate = new DateTime( 2014, 3, 18, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 18, 17, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2533/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<u>Abstract</u>:
<br /> Methane is about 22 times stronger than CO2 as a greenhouse gas and has been accumulated strongly in the atmosphere during the last decades. However, emissions from aquatic systems in Switzerland are not well constrained. We have investigated several lakes and reservoirs in Switzerland and I will show some results of this research. The main topic is how to constrain methane oxidation in freshwater systems. From the ocean we know that most of the methane formed in the sea floor is anaerobically oxidized via sulfate already in the sediments and/or later in the water column. In lakes sulfate concentrations are low and methane could be oxidized either aerobically with oxygen or anaerobically via other electron acceptors (nitrate, iron, manganese). I will show data from freshwater systems in Switzerland to demonstrate how methane is transformed into carbon dioxide using mainly isotopic, mass spectrometer, and incubation methods.
<br />
<br />
<u>Short biography</u>:
<br /> Carsten Schubert is a biogeochemist with a strong expertise in stable isotope geochemistry, organic geochemistry and biogeochemistry. He traces biogeochemical processes and interactions in marine and lacustrine sediments and water bodies. His research has led to over 75 publications. Through the analysis of specific biomarkers (both concentration and isotopic composition) in water column and sediments it was possible to study phytoplankton community structures, demonstrate that CH4 is actively consumed under anaerobic conditions, showthe first conclusive evidence for anammox in a lacustrine system (Lake Tanganyika) or reconstructocean currents off Newfoundland using 15N of amino acids in deep sea corals. Most recently, methane emissions were determined from lakes, reservoirs and rivers; processes related to methane oxidation in the water column of Lake&nbsp; Kivu, several Swiss lakes and in the Arctic Tundra was investigated, finally we work on the degradation/preservation of amino acids and amino sugars in Swiss lakes.
<br />
<br /> Carsten Schubert is head of the department “Surface Waters-Research and Management” at the Swiss Federal Institute of Aquatic Science and Technology (Eawag).",
                            DetailsUrl = null
                        } },
                        { 22, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"No Title",
                            SpeakerName = @"Elise GOURIER (PhD in Finance at the Swiss Banking Institute, University of Zurich)",
                            Location = "Extranef, room 125",
                            StartDate = new DateTime( 2014, 3, 18, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 18, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 23, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"TBA",
                            SpeakerName = @"Ezra Getzler, Northwestern",
                            Location = "MAA331",
                            StartDate = new DateTime( 2014, 3, 17, 15, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 17, 17, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 24, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Molecular and cellular basis of bitter taste",
                            SpeakerName = @"Prof. Wolfgang MEYERHOF, Dept of Molecular Genetics, German Institute of Human Nutrition, Potsdam-Rehbrücke/Germany",
                            Location = "CH G1 495",
                            StartDate = new DateTime( 2014, 3, 17, 14, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 17, 15, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 25, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Investigation of the creep of cementitious materials by indentation",
                            SpeakerName = @"Matthieu Vandamme, École des Ponts ParisTech, France",
                            Location = "MXF1",
                            StartDate = new DateTime( 2014, 3, 17, 13, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 17, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2744/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Concrete creeps (i.e., slowly deforms under constant load) over decades, at a pace that can be detrimental to the lifespan of civil engineering structures. However, in spite of decades of research, the origin of this viscous behavior still remains unclear. Here we use the indentation technique to study this creep of cementitious materials over a variety of length scales. The indentation technique is a handy contact mechanical test that consists in pushing a probe of known geometry and mechanical properties onto the surface of the material to be tested and enables to measure -- among others -- the creep properties of the indented material. We compare minutes-long microindentation creep experiments on cement paste with years-long macroscopic uniaxial creep experiments on concrete: we thus show that the indentation technique enables to measure the rate of long-term creep of cementitious materials orders of magnitude faster than conventional macroscopic testing. We also apply nanoindentation to measure the creep properties of micrometric phases of calcium silicate hydrates (C-S-H), which are the 'glue' of cement paste and are mostly responsible for the viscous behavior of this latter: this study and an analogy with the viscous behavior of soils serve as a basis to discuss the origin of creep.&nbsp;
<br />
<br /> References: M. Vandamme, F.-J. Ulm, Nanoindentation investigation of creep properties of calcium silicate hydrates. Cement and Concrete Research 52: 38-52 (2013). Q. Zhang, R. Le Roy, M. Vandamme, B. Zuber, Long-term creep properties of cementitious materials: Comparing microindentation testing with macroscopic uniaxial compressive testing. Cement and Concrete Research 58: 89-98 (2014).
<br />
<br /> Bio: Curriculum
<br />
<br /> &nbsp;&nbsp;&nbsp; 2008 - now : Researcher at Laboratoire Navier, Ecole des Ponts ParisTech, Universit&eacute; Paris-Est, Champs-sur-Marne, France
<br /> &nbsp;&nbsp;&nbsp; 2004 - 2008 : PhD at Massachusetts Institute of Technology, Cambridge, MA. Thesis on : The nanogranular origin of concrete creep : A nanoindentation investigation of microstructure and fundamental properties of calcium-silicate-hydrates
<br /> &nbsp;&nbsp;&nbsp; 2002-2003 : M.S. at Ecole des Ponts ParisTech, Champs-sur-Marne, France ; M.Eng. in civil engineering at Ecole des Ponts ParisTech, Champs-sur-Marne, France
<br /> &nbsp;&nbsp;&nbsp; 1999-2002 : Bachelor at Ecole Polytechnique, Paris, France
<br />
<br /> Research interests
<br />
<br /> &nbsp;&nbsp;&nbsp; Mechanics and physics of porous solids
<br /> &nbsp;&nbsp;&nbsp; Cementitious materials and geomaterials
<br /> &nbsp;&nbsp;&nbsp; Adsorption-induced deformation
<br /> &nbsp;&nbsp;&nbsp; Nanoindentation technique
<br /> &nbsp;&nbsp;&nbsp; Computed X-ray microtomography",
                            DetailsUrl = @"http://navier.enpc.fr/"
                        } },
                        { 26, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"ITPP Seminar by Prof. Julia Lane, American Institutes for Research",
                            SpeakerName = @"Prof. Julia Lane, Senior Managing Economist, American Institutes for Research",
                            Location = "ODY 4.03 - VIP Room",
                            StartDate = new DateTime( 2014, 3, 17, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 17, 13, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2685/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<strong>&quot;New approaches to examining the production of science</strong>&quot;
<br />
<br />
<br />
<strong>Abstract</strong>:
<br /> Collaboration matters in science.&nbsp; Yet, while there is intriguing evidence that the organization of scientific collaboration is changing, and that teams are becoming more important, the analysis has been mostly based on studying the results of collaborations as evidenced by co-authorship of publications or patents at the scientist level . There are a number of unanswered questions about the structure of the fundamental unit of scientific production: the project team.&nbsp;&nbsp; Who works on scientific teams?&nbsp; What is the role of postdoctoral fellows and graduate and undergraduate students? How do scientific networks of collaboration evolve in response to federal funding decisions? And how do different network structures affect scientific productivity, in both creation of knowledge and diffusion of results?&nbsp; Answering these questions is of fundamental national interest. The federal government invests more than $140 billion annually in basic and applied research.&nbsp; Businesses investment approximately twice that.&nbsp; Yet the way in which these investments work their way through to affecting innovation, economic growth and social well-being remains largely a black box, because so little is known about the project teams that are the fundamental building block of research and development activity. We contribute to the literature by using new longitudinal data derived from the STAR METRICS program to examine a new level of analysis in depth: the structure of scientific collaboration and the use of scientific equipment at the project level and the effect on subsequent outcomes.
<br />
<br />
<strong>Authors</strong>
<br /> Julia Lane, Jacques Mairesse, Michele Pezzoni and Paula Stephan",
                            DetailsUrl = null
                        } },
                        { 27, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Workshop II",
                            SpeakerName = "",
                            Location = "TBA",
                            StartDate = new DateTime( 2014, 3, 17, 8, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 21, 18, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Part of the Tropical geometry in its complex and symplectic aspects semester organized by the CIB.",
                            DetailsUrl = null
                        } },
                        { 28, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"No Title",
                            SpeakerName = @"S. Vish VISWANATHAN (Duke University)",
                            Location = "Extranef, room 126",
                            StartDate = new DateTime( 2014, 3, 14, 10, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 14, 12, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 29, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Thermal decomposition of Cumene Hydroperoxide: chemical and kinetic characterization",
                            SpeakerName = @"Sergio Vernuccio",
                            Location = "Batochime5310",
                            StartDate = new DateTime( 2014, 3, 14, 9, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 14, 10, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2791/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"PhD applicant to EDCH program",
                            DetailsUrl = null
                        } },
                        { 30, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"LCPM Presentation",
                            SpeakerName = @"Ludmila Voronina",
                            Location = "CH H5 625",
                            StartDate = new DateTime( 2014, 3, 14, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 14, 10, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/1985/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 31, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Neuroscience meets cell and molecular biology",
                            SpeakerName = "",
                            Location = "EPFL-SV1717A",
                            StartDate = new DateTime( 2014, 3, 14, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 15, 16, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2694/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<strong>EPFL SV - Hong Kong University of Science &amp; Technology<br /> 1st Joint Symposium<br /><br /> 2 days / 4 sessions / 18 speakers</strong>
<br />
<br /> Chaired by Professors Carmen Sandi, Daniel Constam, Bart Deplancke and Ralf Schneggenburger
<br />
<br /> Information and registration on: 
<a href=""http://bmi.epfl.ch/jointsymposium2014"">bmi.epfl.ch/jointsymposium2014</a>",
                            DetailsUrl = null
                        } },
                        { 32, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Des nouvelles de Spetses, ou GLn(x) pour x une indéterminée?",
                            SpeakerName = @"Prof. Michel Broué, Université Paris Diderot",
                            Location = "CM4",
                            StartDate = new DateTime( 2014, 3, 13, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 13, 18, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2778/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Colloque de Math&eacute;matiques",
                            DetailsUrl = null
                        } },
                        { 33, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"TBD",
                            SpeakerName = @"Peter Carmeliet VIB Vesalius Research Center Katholieke Universiteit Leuven Leuven, Belgium",
                            Location = "SV 1717A",
                            StartDate = new DateTime( 2014, 3, 13, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 13, 13, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/1876/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 34, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"""Science! On tourne"" : La fonte des glaces, un drame magnifique",
                            SpeakerName = @"Robert Bolognesi et Martin Beniston",
                            Location = "Rolex Learning Center",
                            StartDate = new DateTime( 2014, 3, 12, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 12, 13, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2775/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"La fonte des banquise a beau &ecirc;tre un signe de graves probl&egrave;mes climatiques, elle n'en g&eacute;n&egrave;re pas moins des d&eacute;cors saisissants de beaut&eacute;. Robert Bolognesi, nivologue et photographe, est parti &agrave; maintes reprises &agrave; l'assaut des p&ocirc;les. Il en a ramen&eacute; de superbes images dont certaines seront expos&eacute;es du 20 f&eacute;vrier au 27 mars au Rolex Learning Center dans le cadre de l'exposition ICE - Voyage au pays des icebergs.
<br />
<br /> C&eacute;l&egrave;bre climatologue, Martin Beniston a lui aussi parcouru les p&ocirc;les et constat&eacute;, de ses yeux, la disparition de la banquise.
<br />
<br /> Comment faire dialoguer un regard artistique et une approche scientifique autour d'un tel ph&eacute;nom&egrave;ne? Robert Bolognesi et Martin Beniston se pr&ecirc;teront au jeu &agrave; l'occasion d'une nouvelle &eacute;dition de &quot;Science! on tourne&quot; le mercredi 12 mars 2014, &agrave; 12h15, au caf&eacute; Klee du Rolex Learning Center (vid&eacute;o disponible sur le site web de l'EPFL apr&egrave;s la rencontre).",
                            DetailsUrl = null
                        } },
                        { 35, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Responses of soil bacterial and fungal communities to summer desiccation and rewetting in Mediterranean grasslands: summer precipitation pattern matters",
                            SpeakerName = @"Dr Romain Barnard, Groupe de recherche Agroecologie, INRA Dijon, FR",
                            Location = "GR A3 32",
                            StartDate = new DateTime( 2014, 3, 11, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 11, 17, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2532/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<u>Abstract:</u>
<br /> The massive soil CO2 efflux associated with rewetting dry soils after the dry summer period significantly contributes to the annual carbon budget of Mediterranean grasslands. Better understanding i) soil microbial adaptation strategies to a seasonally contrasted climate and ii) the response of microorganisms to altered summer precipitation is important to predict future changes in carbon cycling in Mediterranean grasslands. These two axes of research were tackled in both present (DNA-based) and potentially active (rRNA-based) soil bacterial and fungal communities, by sequencing phylogenetic marker genes and quantifying the abundance of these genes and transcripts.
<br />
<br /> How do soil microorganisms respond to extreme desiccation and rewetting? In three California annual grasslands, soil microbial communities were tracked over a summer season, and in response to controlled rewetting of intact soil cores. Contrasting desiccation-related bacterial life-strategies that were consistent across sites suggest that predicted changes in precipitation patterns may affect soil nutrient and carbon cycling by differentially impacting activity patterns of microbial communities.
<br />
<br /> How do changes in summer precipitation pattern affect soil microbial response to fall wet-up? Intact soil cores were subjected to three different precipitation patterns over four months (full summer dry season, extended wet season, and absent summer dry season), then the effects of a controlled rewetting event on the soil CO2 efflux pulse and on soil microbial communities were investigated. We found a strong, significant positive relation between change in the structure of the potentially active bacterial community and the magnitude of the CO2 pulse upon rewetting dry soils. We suggest that the duration of severe dry conditions may be important in conditioning the response of the potentially active bacterial community to wet-up and in controlling the magnitude of the CO2 pulse resulting from wet-up events. Thus, predicted changes in summer precipitation pattern may likely affect the metabolic potential of the soil bacterial community and the related magnitude of the rainfall-induced CO2 pulse upon rewetting.
<br />
<br />
<u>Short biography</u>
<br /> As&nbsp; a functional ecologist, Romain Barnard investigates the response of terrestrial ecosystem functioning to global change. His research activities are directed towards better understanding the responses of soil C and N cycles in relation to plant physiology. After a Ph.D. at Paris-Sud University on the effects of elevated CO2 on soil nitrogen cycling, he worked at ETH Z&uuml;rich for six years in Nina Buchmann’s lab, using stable isotopes to study the controls of time-lags between the assimilation of carbon by the plant and its respiration by the soil. More microbiological aspects of the effects of changes in precipitation patterns on ecosystem functioning were later developed during a two-year stay at University of California, Berkeley in Mary Firestone’s lab. He is now a research scientist at the INRA (French National Institute for Agricultural Research) in Dijon, where he focuses on the effects of precipitation patterns on plant-soil interactions.",
                            DetailsUrl = null
                        } },
                        { 36, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Radioscopy of Fatigue Cracks",
                            SpeakerName = @"Prof. François Hild, Laboratory of Mechanics and Technology, Cachan (France) Bio : François Hild is Research Professor at the Laboratory of Mechanics and Technology in Cachan (France).  He graduated from École Normale Supérieure de Cachan in 1989.  He received his Ph.D. degrees in Mechanical Engineering from the University of Paris 6 in 1992 and from the University of California in 1995, and his habilitation from the University of Paris 6 in 1998.  His research interests include advanced experimental techniques, digital image and volume correlation, identification and validation procedures for material models.",
                            Location = "ME B1 10",
                            StartDate = new DateTime( 2014, 3, 11, 13, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 11, 14, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2796/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<u>Abstract</u> : 3D imaging techniques (e.g., X-ray computed microtomography or XRCMT) of in situ mechanical tests allow for the measurement of 3D displacement fields by resorting to digital volume correlation.&nbsp; More importantly, it enables for the identification of mechanical properties of opaque materials.&nbsp; When using XRCMT reconstructed volumes, measurement uncertainties and correlation residuals are assessed when finite element based volume correlation is used. In particular, artifacts related to the use of lab tomographs are discussed.
<br /> For a tensile test of cracked cast iron sample imaged by XRCMT, a fully coupled experimental / numerical procedure is based on an enriched kinematic basis as in eXtended finite element analyses.&nbsp; The crack surface, crack front, and stress intensity factor profiles are extracted from the measured displacements and compared with numerical predictions.&nbsp; It is also possible to evaluate local crack propagation features by analyzing different propagation steps.&nbsp; This type of analysis shows that there are direct ways to bridge the gap between experiments and simulations thanks to consistent kinematic bases and identification procedures, and opens the way for the validation of fracture laws and numerical models.",
                            DetailsUrl = null
                        } },
                        { 37, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Architecture souterraine vs génie civil: possibilités et limites",
                            SpeakerName = @"Miguel Gómez Navarro, ingénieur civil, dr ès sciences techniques EPFL",
                            Location = "AAC 1 14",
                            StartDate = new DateTime( 2014, 3, 11, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 11, 10, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2797/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Conf&eacute;rence de Miguel G&oacute;mez Navarro, Directeur de l'Ecole d'architecture de l'Universit&eacute; Europ&eacute;enne&nbsp;de Madrid",
                            DetailsUrl = null
                        } },
                        { 38, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Partial Fourier-Mukai transform for algebraically integrable systems",
                            SpeakerName = @"Roman Fedorov, Kansas State",
                            Location = "MAA331",
                            StartDate = new DateTime( 2014, 3, 10, 15, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 10, 17, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The celebrated Fourier-Mukai transform is an equivalence between the
<br /> derived category of an abelian variety and that of the dual abelian
<br /> variety. Recently there have been a lot of interest in Fourier-Mukai
<br /> transforms for singular degenerations of abelian varieties, e.g., for
<br /> Jacobians of singular curves. However, very little is known beyond the
<br /> Jacobian case. In a joint work with D. Arinkin we suggest a different
<br /> setup. Let p:X-&gt;B be a flat morphism of smooth complex varieties with
<br /> integral projective fibers. We also assume that X is symplectic and the
<br /> smooth locus of each fiber is Lagrangian (thus, we do not assume that
<br /> the fibers are smooth). We argue that in this case p:X-&gt;B is an
<br /> algebraically completely integrable system. We construct the smooth part
<br /> of the 'dual integrable system' and construct the corresponding partial
<br /> Fourier-Mukai transform. Applications to Hitchin systems will be discussed.",
                            DetailsUrl = null
                        } },
                        { 39, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Information Release and the Fit of the Fama-French Model",
                            SpeakerName = @"Thomas GILBERT (University of Washington)",
                            Location = "Extranef, room 118",
                            StartDate = new DateTime( 2014, 3, 10, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 10, 13, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 40, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"New metallic materials for biodegradable implant applications",
                            SpeakerName = @"Jörg Löffler, ETHZ, Zürich",
                            Location = "MXF1",
                            StartDate = new DateTime( 2014, 3, 10, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 10, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2743/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Biomedical implants used in osteosynthesis and vascular intervention are usually made of metallic materials. They are designed to have high corrosion resistance in order to remain permanently in the body, as e.g. metallic stents in vascular intervention; or they must be removed in a second surgery, as e.g. metallic screws and plates in broken bone fixation. Permanent implants often generate problems such as prolonged physical irritation and chronic inflammation, and can only be applied in pediatric surgery to a limited extent because they do not grow with the patient. To overcome the limitations&nbsp;of permanent devices we have developed new classes of biodegradable metallic alloys which&nbsp;degrade in the body after performing their task. I will describe our efforts in the development of amorphous and crystalline Mg-&shy;?alloys, based on metal physical design rules, and present their mechanical properties, in-&shy;?vitro and in-&shy;?vivo degradation performance, and biological response [1-&shy;?3]. MgZnCa alloys in particular are suitable for use as biodegradable implants because they have suitable mechanical properties, are biocompatible, show good osteoconductivity and osteoinductivity, and exhibit adjustable degradation rates.
<br />
<br /> References:
<br /> [1] B. Zberg, P. J. Uggowitzer, J. F. L&ouml;ffler, ‘MgZnCa glasses without clinically observable hydrogen evolution for
<br /> biodegradable implants’, Nature Mater.&nbsp; 8 (2009) 887 – 891.
<br /> [2] T. Kraus, S. F. Fischerauer, A. C. H&auml;nzi, P. J. Uggowitzer, J. F. L&ouml;ffler, A. M. Weinberg, ‘Magnesium alloys for
<br /> temporary implants in osteosynthesis: In-&shy;?vivo studies of their degradation and interaction with bone’, Acta
<br /> Biomater.&nbsp; 8 (2012) 1230 – 1238.
<br /> [3] J. Hofstetter, M. Becker, E. Martinelli, A. M. Weinberg, B. Mingler, H. Kilian, S. Pogatscher, P. J. Uggowitzer,
<br /> J. F. L&ouml;ffler, ‘High-&shy;?strength low-&shy;?alloy (HSLA) Mg–Zn–Ca alloys with excellent biodegradation performance’, J. of Metals (JOM) (2014), DOI: 10.1007/s11837-&shy;?014-&shy;?0875-&shy;?5.
<br />
<br /> Bio : J&ouml;rg F. L&ouml;ffler has been Professor at the Department of Materials, ETH Zurich, since July 2003. Starting as Assistant Professor, in 2007 he was elected Full Professor of Metal Physics and Technology. From 2010 – 2013 he was Chairman of the Department of Materials.
<br />
<br /> Born in Germany in 1969, J&ouml;rg L&ouml;ffler studied Physics and Materials Science at Saarland University, Germany. Following diploma thesis work at the Institute of New Materials in Saarbrucken with Prof. Herbert Gleiter (1994), he transferred to the Paul Scherrer Institute and ETH Zurich, where he earned his doctorate in the magnetism of nanostructured materials and neutron scattering (1997). After a short stay as a postdoctoral researcher at the Paul Scherrer Institute, L&ouml;ffler took up a post at the California Institute of Technology as an Alexander von Humboldt Fellow, where he worked with Prof. William L. Johnson in the area of bulk metallic glasses. In 2001 he was appointed tenure-track Assistant Professor at the University of California, Davis, where he stayed until his appointment to ETH Zurich in 2003. Since November 2007 J&ouml;rg L&ouml;ffler has been Full Professor of Metal Physics and Technology.
<br />
<br /> The principal areas of J&ouml;rg L&ouml;ffler’s research are the synthesis and characterization of novel nanostructured and amorphous materials; magnetic, optical, and mechanical properties on the nanoscale; the use of metals for medical applications (in particular bioresorbable implants); and neutron scattering and synchrotron radiation. His work has received distinctions at several international conferences. Other awards include the ETH Zurich Medal for the excellence of his Ph.D. dissertation and the Masing Memorial Award for his contributions to materials science. J&ouml;rg L&ouml;ffler was a member of the German National Merit Foundation from 1991 to 1997, holds a Visiting Faculty position at the California Institute of Technology, and is Adjunct Professor at the World Premier Institute (WPI) of Tohoku University in Sendai. He is a member of the editorial boards of Intermetallics, Metallurgical and Materials Transactions A, Journal of Nanoscience and Nanotechnology, and the open-access journal Results in Physics. He is also member of the Science Advisory Council of the European Spallation Neutron Source.",
                            DetailsUrl = @"http://www.metphys.mat.ethz.ch/"
                        } },
                        { 41, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Brain Awareness Week 2014",
                            SpeakerName = "",
                            Location = "CHUV - Auditoire César-Roux",
                            StartDate = new DateTime( 2014, 3, 10, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 14, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 42, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Removing unwanted variation: from principal components to random effects",
                            SpeakerName = @"Prof. Terry Speed, Walter and Eliza Hall Institute of Medical Research and UC Berkeley",
                            Location = "CE2",
                            StartDate = new DateTime( 2014, 3, 7, 15, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 16, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2772/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Statistics Seminar",
                            DetailsUrl = null
                        } },
                        { 43, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Mind and Magic: Manipulating Perception Through Physical Misdirection",
                            SpeakerName = @"Mark Mitton",
                            Location = "SV 1717A",
                            StartDate = new DateTime( 2014, 3, 7, 14, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 16, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2800/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Physical misdirection is the foundation on which all magic - tricks, illusions, escapes, pick pocketing, mind reading, and even physical comedy – is based.&nbsp;It operates on the relationship between the actual and the perceived in a tactical way. &nbsp;This kind of magic offers a specific way to track awareness, arousal and identity as an experience that is not necessarily verbal. &nbsp;World-famous magician Mark Mitton invites us to experience physical misdirection for ourselves from both the audience's and practitioner's perspectives.&nbsp;Mitton will give a talk about physical misdirection - his specialty within magic - and draw connections to themes from cognitive psychology like attentional selection and inattentional blindness. He will engage us in thinking about how physical misdirection can be useful to scientists, psychologists and philosophers who are interested in perception, memory and perhaps even consciousness.
<br />
<br /> About Mark Mitton:
<br />
<br /> Mark Mitton is a world-famous professional magician who is fascinated by using magic to better understand how we see the world. For the past 25 years, Mitton has not only been dazzling audiences with his amazing skills, but has also been involved in investigating magic’s connections with psychology, biology, neurology, and philosophy. A proponent of the renowned cognitive psychologist Urich Neisser and apprentice to the famous magician magician Slydini, Mitton is just as eager to engage with psychologists and neuroscientists as he is to perform mind-bending feats of physical misdirection. He has co-organized and/or participated in a number of meetings with leading cognitive psychologists (Eldar Shafir, Joseph LeDoux, Nicholas Turk-Brown, and others) around the theme of mind and magic. Mitton has also entertained at private events of Beyonce and Sting,&nbsp; done opening acts for Aerosmith, made Will Smith appear in the middle of Times Square, taught sleight of hand to John Travolta and Stanley Tucci, worked with the biologist and Nobel laureate Gerald Edelman,&nbsp; performed in honor of Frank Yang, and recently moderated a panel of art restorers and criminal investigators who were addressing the theme of fraud, forgery, illusion, and the counterfeit.
<br />
<br /> Mitton's web site: 
<a href=""http://www.markmitton.com"">www.markmitton.com</a>
<br /> A BBC documentary with Mark Mitton stealing people's wrist watches: 
<a href=""http://www.youtube.com/watch?v=ToDMG7ptZss"">http://www.youtube.com/watch?v=ToDMG7ptZss</a>
<br /> Mitton's CV (attached to announcement on memento.epfl.ch)",
                            DetailsUrl = null
                        } },
                        { 44, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Nanowire superconducting single-photon detectors: towards the ultimate optical detector",
                            SpeakerName = @"Prof. Andrea Fiore, COBRA Research Institute, Eindhoven Univ. Technol., The Netherlands",
                            Location = "PH L1 503",
                            StartDate = new DateTime( 2014, 3, 7, 14, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2808/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"In this talk single-photon detectors based on superconducting nanowires will be described, which provide unmatched sensitivity, speed and temporal resolution at telecom wavelengths. After a general overview of the operation principle and performance, the physical processes governing the detector's single- and multi-photon response will be addressed.&nbsp; Novel detector concepts leading to more advanced functionalities, such as photon-number-resolution, will also be discussed.",
                            DetailsUrl = @"http://www.tue.nl/en/university/departments/electrical-engineering/research/research-institutes/research-institute-cobra/"
                        } },
                        { 45, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Supramolecules, Nanoswitches and Nanorotors in Action",
                            SpeakerName = @" Prof. Michael Schmittel, University of Siegen (Germany), Dep. of Organic Chemistry I",
                            Location = "BCH 3118",
                            StartDate = new DateTime( 2014, 3, 7, 14, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 15, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2726/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<a href=""http://www.uni-siegen.de/fb8/oc/oc1"">www.uni-siegen.de/fb8/oc/oc1</a>
<br type=""_moz"" />",
                            DetailsUrl = null
                        } },
                        { 46, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Flexibility in Engineering Design",
                            SpeakerName = @"Prof. Richard de Neufville (MIT)",
                            Location = "GC B330",
                            StartDate = new DateTime( 2014, 3, 7, 12, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 13, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2807/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Flexibility in Design is an effective way to manage uncertainty. It enables system managers to adapt to evolving environments, to avoid bad situations and take advantage of emerging good opportunities.
<br /> The approach is strategic. It views systems management as a dynamic process in which designers necessarily add or change capacities and capabilities over time. As in chess, the key to success lies in positioning the system to provide valuable options. Flexibility in design is most desirable when the future is most uncertain, exactly when options are most valuable.
<br /> Flexibility in Design contrasts with Robust design. Rather than minimize the variation of future system performance, Flexibility in Design redistributes the variation in performance, reducing the downside possibilities while maximizing upside potential.
<br /> The presentation outlines the process of achieving Flexible Designs, and demonstrates its operation and value through examples. The analysis maximizes overall expected system value. It starts with explicit recognition of underlying uncertainties – in sharp contrast to conventional systems design based on fixed system requirements.&nbsp; The process explores the distribution of possible outcomes associated with alternative design concepts, generally by Monte Carlo simulations.
<br /> Example applications indicate that Flexibility in Design routinely leads to 10 to 30% increases in expected value. The intuition is that it guards against the highest risks (a win), enables taking advantage of new opportunities (more win), while frequently reducing immediate capital costs (by deferring decisions on capacity and function).&nbsp; The net effect in general is: more wins at lower cost!
<br />
<br /> Bio: Dr. Richard de Neufville is Professor of Engineering Systems and of Civil and Environmental Engineering at MIT. He specializes in Systems Analysis and Design of major infrastructure. His work now focuses on flexibility in technological systems. This is logically equivalent to using “real options”, but in engineering the analysis differs substantially from that of financial options. This approach implies a fundamental shift in engineering design, from a focus on fixed specifications, to a concern with system performance under the range of possible risks and opportunities. He has worked widely – geographically and substantively -- on many projects, including hydropower, oil platforms, copper mines, and his substantive specialty, airports. He is author of Flexibility in Engineering Design (MIT Press, 2011), Airport Systems Planning, Design and Management, (McGraw-Hill, 2nd edition, 2013); Applied Systems Analysis (McGraw-Hill) and other texts. Numerous prizes have recognized his work, including the Sizer Award for the Most Significant Contribution to MIT Education for having founded and led the MIT Technology and Policy Program. He has an MIT PhD and a Delft Dr.Hc. Born in the United States, he enjoyed 7 years of school in Switzerland.",
                            DetailsUrl = null
                        } },
                        { 47, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Epidermal-dermal communication in healthy skin and in cancer",
                            SpeakerName = @"Dr. Fiona Watt",
                            Location = "SV 1717a",
                            StartDate = new DateTime( 2014, 3, 7, 11, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 12, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2735/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 48, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"No Title",
                            SpeakerName = @"Peter DE MARZO (Stanford GBS)",
                            Location = "Extranef, room 126",
                            StartDate = new DateTime( 2014, 3, 7, 10, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 12, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 49, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Mastering the Design of Modern Complex Distributed Systems",
                            SpeakerName = @"Dr. Iuliana Bacivarov, ETHZ",
                            Location = "MEB1 B10",
                            StartDate = new DateTime( 2014, 3, 7, 10, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2795/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Ever-increasing needs of society for richer functionality have catalyzed the emergence of novel highly-distributed, highly-parallel systems. On-chip multi- and many-core systems embedding up to hundreds of heterogeneous cores are nowadays a reality. Other paradigms such as Cyber-Physical Systems (CPS), Autonomous Sensor Networks and Swarms may foster as yet unforeseen applications, by providing ubiquitous, distributed sensing, processing, communication, and actuation. Smart medical devices, smart houses, intelligent environmental and energy monitoring, intelligent transportation, social networks, interconnected together and interacting with each other may soon become standard applications governing our lives. The potential economical and societal impact of such systems is huge, and major investments are being made worldwide to develop the technology.
<br />
<br /> The past 10 years of my research cover advances on embedded many-core systems. Specifically, system-level design strategies for specifying, analyzing, and optimizing such complex distributed systems were investigated. The key challenge is how to effectively exploit the entire available computational power, and optimize together timing, temperature, and power consumption objectives. Two main aspects were considered (1) when hardware parallelism cannot be fully exploited due to unpredictable, dynamic applications competing for resources or due to a sub-optimal mapping caused by the huge dimensionality of the design space and (2) when computational power does not necessarily translate into high performance as the system runs into the thermal wall. The presentation will answer to these two challenges and will present practical implications when integrating the proposed aspects in a complete and automatic system-level design flow, namely the Distributed Application Layer (
<a href=""http://www.tik.ee.ethz.ch/~euretile"">http://www.tik.ee.ethz.ch/~euretile</a>) that we have developed at ETH Z&uuml;rich. My ambition for the next 10 years is to make Cyber-Physical Systems (including Autonomous Systems, Swarms, Smart Systems, and so on) a reality that would drastically improve our lives, relying on new technological advances.
<br />
<br /> Bio: Dr. Iuliana Bacivarov is a senior research scientist in the Computer Engineering and Networks Lab at ETH Z&uuml;rich, which she joined as a post-doc in 2006. Before that, she received her M.Sc. and Ph.D. degrees in micro and nano-electronics from National Polytechnic Institute of Grenoble, France, in 2003 and 2006, respectively, and her B.Sc. in electrical engineering from National Polytechnic Institute of Bucharest, Romania, in 2002.
<br />
<br /> Dr. Bacivarov’s main research interests are focused on the optimization of applications distributed onto massively parallel architectures, targeting both embedded systems and high performance computing systems. Her work addresses system-level models and methods for programming, analyzing the performance, and optimizing such systems as part of large scale EU-funded projects: SHAPES, EURETILE, PRO3D, COMBEST, and PREDATOR and Swiss nano-tera.ch founded projects: Ultrasound To Go and Extreme. She is coordinating project activities in SHAPES (January 2006-December 2009), EURETILE (started January 2010), and PRO3D (January 2010 – December 2012). Dr. Bacivarov has authored or co-authored more than 50 publications, of which two have received best paper awards and two have been nominated. She is an established researcher in her community, having given more than 35 talks at prestigious international conferences such as DAC, DATE, and ES Week, of which 26 have been invited.",
                            DetailsUrl = null
                        } },
                        { 50, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Discussion meeting",
                            SpeakerName = "",
                            Location = "CH H5 625",
                            StartDate = new DateTime( 2014, 3, 7, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 10, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/1975/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 51, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Oxidative Amination with and without Metals",
                            SpeakerName = @"Prof. Kilian Muniz, ICIQ, Tarragona, Spain",
                            Location = "BCH 2218",
                            StartDate = new DateTime( 2014, 3, 5, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 5, 18, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2596/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The first part of the lecture will focus on concepts for palladium catalysed synthesis of vicinal diamines from alkenes.1,2 Key methodology is the use of high oxidation state metal catalysis such as palladium(IV) catalysis.3 These reactions have been elaborated for terminal and internal alkenes with and proceed under complete regio- and diastereoselectivity.4,5 The final products can be converted readily into different ligand motifs.
<br />
<br /> Within our ongoing programme to develop new metal-free amination reactions, we have recently reported novel transformations using defined hypervalent iodine reagents in combination with bissulfonimides as nitrogen sources.6 Unprecedented transformations of this type include the first enantioselective diamination of alkenes (eq. 2)7,8 and first examples of selective diamination of butadienes.9 Related transformations including allylic amination10 and amination of acetylenes11 will also be discussed.
<br /> __________________
<br /> (1) A. Iglesias, E. G. P&eacute;rez, K. Mu&ntilde;iz, Angew. Chem. Int. Ed. 2010, 49, 8109.
<br /> (2) K. Mu&ntilde;iz, J. Kirsch, P. Ch&aacute;vez, Adv. Synth. Catal. 2011, 353, 689.
<br /> (3) K. Mu&ntilde;iz, Angew. Chem. Int. Ed. 2009, 48, 9412.
<br /> (4) C. Mart&iacute;nez, K. Mu&ntilde;iz, Angew. Chem. Int. Ed. 2012 51, 7031.
<br /> (5) &Aacute;. Iglesias, R. &Aacute;lvarez, &Aacute;. R. de Lera, K. Mu&ntilde;iz, Angew. Chem. Int. Ed. 2012 51, 2225.
<br /> (6) J. A. Souto, C. Mart&iacute;nez, I. Velilla, and K. Mu&ntilde;iz, Angew. Chem. Int. Ed. 2013, 52, 1324
<br /> (7) C. R&ouml;ben, J. A. Souto, Y. Gonz&aacute;lez, A. Lishchynskyi, K. Mu&ntilde;iz, Angew. Chem. Int. Ed. 2011, 50, 9478.
<br /> (8) J. Souto, Y. Gonz&aacute;lez. A. Iglesias, D. Zian, A. Lishchynskyi, K. Mu&ntilde;iz, Chem. Asian J. 2012, 7, 1103.
<br /> (9) A. Lishchynskyi, K. Mu&ntilde;iz, Chem. Eur. J.. 2012, 18, 2213.
<br /> (10) J. A. Souto, D. Zian, K. Mu&ntilde;iz, K. J. Am. Chem. Soc. 2012, 134, 7242.
<br /> (11) J. A. Souto, P. Becker, A. Iglesias, K. Mu&ntilde;iz, K. J. Am. Chem. Soc. 2012, 134, 15505.",
                            DetailsUrl = null
                        } },
                        { 52, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Adaptive low-rank methods for high dimensional elliptic partial differential equations",
                            SpeakerName = @"Dr. Markus BACHMAYR (Institut für Geometrie und Praktische Mathematik, RWTH Aachen / Germany)",
                            Location = "MA A3 30",
                            StartDate = new DateTime( 2014, 3, 5, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 5, 17, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2608/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Seminar of Numerical Analysis",
                            DetailsUrl = @"http://mathicse.epfl.ch/seminars"
                        } },
                        { 53, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Improving system energy efficiency by leveraging context awareness",
                            SpeakerName = @"Prof. Tajana Simuni? Rosing, University of California, San Diego",
                            Location = "ME B1 B10",
                            StartDate = new DateTime( 2014, 3, 5, 14, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 5, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2790/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The proliferation of personal computing and the advent of cheap, small sensors have given the rise of computing at the edge of the traditional computational infrastructure.&nbsp; While various technological components of the computing systems at the edge of internet already exist, the key to success of this new class of systems are advances in the abstractions that can support easy extensibility, and development of adaptive energy management strategies that ensure efficient system operation.&nbsp; In this talk I first give an overview of the systems and algorithms we have developed at UCSD to enable the development of adaptive edge computing infrastructure along with strategies that significantly lower the energy consumption in sensing, mobile and edge server infrastructures. The rest of the talk focuses on how context can be leveraged to enhance the system operation.&nbsp; Context is defined as any relevant information that provides a value-add to improving energy efficiency. Source-side context, in the form of environmental variables, can help predict solar and wind output, while load-side context, such as workload analysis and prediction, can tailor the load to accommodate the energy source variability. We show how we can leverage context in two applications: data centers and individual residences with on-site renewables and distributed energy storage. By applying context data to automation, prediction, and smart scheduling, we demonstrate over 90% improvement in green energy efficiency for data centers, and over 40% improvement in green energy costs in residences when compared with using no context.
<br />
<br /> Bio: Tajana Simuni? Rosing is currently an Associate Professor of Computer Science and Adjunct Associate Professor in the Electrical and Computer Engineering Department at UCSD.&nbsp; She is currently heading the effort in SmartCities as a part of DARPA and industry funded TerraSwarm center. Prior to that she led the energy efficient datacenters theme as a part of the MuSyC center.&nbsp; Her research interests are energy efficient computing, embedded and large scale distributed systems.&nbsp; Prior to this she was a full time researcher at HP Labs while being leading research part-time at Stanford University.&nbsp; She finished her PhD in 2001 at Stanford University, concurrently with finishing her Masters in Engineering Management.&nbsp; Her PhD topic was Dynamic Management of Power Consumption.&nbsp; Prior to pursuing the PhD, she worked as a Senior Design Engineer at Altera Corporation.",
                            DetailsUrl = null
                        } },
                        { 54, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"BMI Seminar // Vision Assessment on Mobile Devices",
                            SpeakerName = @"Peter Bex Schepens Eye Research Institute, Department of Ophthalmology, Harvard Medical School, Boston, USA",
                            Location = "SV1717A",
                            StartDate = new DateTime( 2014, 3, 5, 12, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 5, 13, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2545/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 55, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Pizzas&Start-up",
                            SpeakerName = @": Pix4D - Olivier Küng, Co-Founder & Sonja Betschart, CMO Citiviz - Nicolas Lachance-Bernard, CEO Gait Up - Benoît Mariani, CEO Fastree 3D - Pierre-Yves Cattin, Co-Founder",
                            Location = "EPFL Innovation Park - Room Uranus",
                            StartDate = new DateTime( 2014, 3, 5, 11, 45, 0 ),
                            EndDate = new DateTime( 2014, 3, 5, 13, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2728/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The 4 Start-ups will pitch their activities in few minutes!
<br /> The occasion to discover what happens around entrepreneurship at EPFL.
<br /> Presentations will be followed by a networking “pizzas-lunch”.
<br /> Mandatory registration on 
<a href=""http://vpiv.epfl.ch/pizzas-start-up-en"">http://vpiv.epfl.ch/pizzas-start-up-en</a>",
                            DetailsUrl = @"http://inform.epfl.ch/index.php?form=PS_March_5_2014"
                        } },
                        { 56, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"OPERA@NOH-Theater",
                            SpeakerName = @"Affaires culturelles et artistiques de l'EPFL & Cercle Suisse-Japon",
                            Location = "Forum Rolex",
                            StartDate = new DateTime( 2014, 3, 4, 19, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 22, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2556/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The French opera “Act&eacute;on” and the Italian intermezzo “Livietta e Tracollo” will be interpreted and enacted in the style of the Japanese noh theater tradition.
<br />
<br /> The show is one of the main events organized to commemorate the 150th anniversary of Swiss-Japanese diplomatic relations. The Swiss tour will be carried out under the patronage of the Japanese Embassy in Switzerland.
<br />
<br /> Tickets to the show in the Rolex Learning Center Forum will be on sale in February 2014. At that time, 200 free tickets will be raffled off to EPFL students and collaborators on EPFL’s culture BLOG.",
                            DetailsUrl = @"http://culture.epfl.ch/opera-noh-theater_en"
                        } },
                        { 57, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Geomicrobiology of an acid impacted uranium mine in Kazakhstan",
                            SpeakerName = @"Dr Brian Reinsch, GR-CEL, CEMBL EPFL",
                            Location = "GR A3 32",
                            StartDate = new DateTime( 2014, 3, 4, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 17, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2408/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<u>Abstract</u>:
<br /> Acidic in situ mining of uranium is used increasinglyin the deserts of Central Asia (e.g., Mongolia, Kazakhstan) in order to supply the world with sufficient uranium for energy generation. Acid is injected into the subsurface and allows rapid recovery of uranium. But, what happens to the acid after mining stops? We investigate the environmental processes that affect the mobilityof the resulting acidic plume, and will determine the legacy of in situ mining. The native microbialcommunities, associated with the ore body, will undergo changes during acid mining which may affect the eventual pH and Eh restoration and subsequent immobilization of uranium. Therefore, the broad objectives of this study are: 1) to determine how the acid affects the Kazakh aquifer, 2) to determine what microbial communities exist pre-, during-, and post-mining, and 3) to determine possible strategies to enhance environmental restoration.
<br />
<br />
<u>Short Biography</u>:
<br /> Brian Reinsch is a postdoctoral researcher at EPFL (ENAC, CEL) originally from San Diego, California. He received a B.S. in chemistry, and minor in mathematics from Chapman University (Orange, CA) and both a M.S. and Ph.D. from the Department of Civil and Environmental Engineering at Carnegie Mellon University (Pittsburgh, PA) under Greg Lowry. Throughout his career the environmental fate, transformations, and effects of heavy metals has been the main focus of his research. This current work is a collaboration between academia (EPFL) and industry (AREVA Mines in France).",
                            DetailsUrl = null
                        } },
                        { 58, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Trapped Reeb orbits do not imply periodic ones",
                            SpeakerName = @"Nena Röttgen (Freiburg i. Br.)",
                            Location = "MA A3 31",
                            StartDate = new DateTime( 2014, 3, 4, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 17, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<strong>Geometry and Dynamics Seminar<br /><br /> Abstract: </strong>The aim of the talk will be the construction of a contact form on R^{2n + 1} with n greater or equal to 2, yielding the standard contact structure, that is standard outside a compact set and induces a Reeb vector field with a compact invariant set, but no closed orbit. This gives a counterexample to a conjecture by H. Hofer. I will provide an overview of connected results. These will include a global Darboux Theorem by Eliashberg and Hofer showing that Reeb flows in higher dimension differ substantially from Reeb dynamics in dimension three.
<br />",
                            DetailsUrl = @"http://cag.epfl.ch/page-39531-en.html"
                        } },
                        { 59, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"xxx",
                            SpeakerName = @"Dr Susan Gaskin, Civil Engineering and Applied Mechanics, Mc Gill University, CA - visiting professor to CRYOS",
                            Location = "GR A3 32",
                            StartDate = new DateTime( 2014, 3, 4, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 17, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2531/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 60, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Targeted Nanomedicines for the Resolution of Inflammation",
                            SpeakerName = @"Nazila Kamaly, Ph.D., Harvard Medical School, MIT Koch Institute, Brigham and Women’s Hospital, Boston, MA (USA) Bio: - MSci, Medicinal Chemistry, University College London, U. of London (2002) - Chemical Data Editor, Chapman & Hall/CRC Press, UK Publishing House (2003) - PhD degree, Imperial College London, Department of Chemistry (2007) - Postdoc Chemist, Medical Research Council, Hammersmith Hospital (2007-09) - Research Associate, Imperial College London, Comprehensive Cancer Imaging Centre (2009-10) - Postdoctoral Research Fellow, Brigham Women's Hospital, Harvard Medical School and David H. Koch Institute for Integrative Cancer Research at MIT, Boston, MA, USA (2011-current) - Instructor, Harvard Medical School & Brigham and Women's Hospital, Boston, MA, USA (2013-current)",
                            Location = "SV1717A",
                            StartDate = new DateTime( 2014, 3, 4, 14, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 15, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2809/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Bioengineering Recruiting Seminar (STI search in 
<a href=""http://bioengineering.epfl.ch/files/content/sites/professeurs/files/shared/EPFL_Immunoengineering_poster.pdf"">Immunoengineering</a>)
<br />
<br />
<strong>Abstract:</strong>
<br /> Inflammation is an essential biological response that is required for tissue homeostasis after injury or infection. Chronic inflammation however is destructive, can lead to tissue damage, and is a hallmark of many diseases such as arthritis, cardiovascular disease, and cancer. New potential therapeutic targets in addressing diseases associated with unresolved inflammation and their underlying mechanisms are now being investigated and understood, making therapeutics which dampen inflammation and enhance resolution of considerable interest -&nbsp; in particular those which can achieve this in a controlled manner with minimal host collateral damage.
<br /> Nanomedicine encompasses a multidisciplinary approach to drug delivery and has been driven in large part by discoveries in nanomaterials design and engineering, which have enabled the accomplishment of the following milestones: 1) development of biodegradable nanocarriers for the delivery of molecules of various sizes and solubilities, in sufficient loading amounts for therapeutic efficacy, 2) selective accumulation of therapeutics at disease sites and improved drug pharmacokinetic, biodistribution and degradation profiles due to careful optimization and engineering of nanoparticle biophysicochemical properties, 3) the ability to target any disease at the organ, tissue, cellular and sub-cellular levels, and 4) emerging clinical successes spanning a 40 year period.
<br /> This talk will present investigations into the development of targeted anti-inflammatory controlled-release polymeric nanomedicines for the treatment of inflammation driven diseases including atherosclerosis and colitis. The synthesis, nanoengineering, characterization and 
<em>in vivo</em> biological investigations of polymeric nanoparticles containing a payload of biologic drugs including a potent mediator biomimetic peptide and the anti-inflammatory cytokine IL-10 will be presented.",
                            DetailsUrl = @"http://bioengineering.epfl.ch/files/content/sites/ibi/files/shared/pdf/140304_Kamaly.pdf"
                        } },
                        { 61, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Triangulated lattice polytopes",
                            SpeakerName = @"Victor Batyrev  (University of Tübingen)",
                            Location = "BI A0 448",
                            StartDate = new DateTime( 2014, 3, 4, 14, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 25, 15, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2776/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Horaires &agrave; consulter sur notre website.
<br />
<br />",
                            DetailsUrl = @"http://bernoulli.epfl.ch/PublicEvent.php?event=1036"
                        } },
                        { 62, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"On the Impact of Hidden Liquidity on Upstairs and Downstairs Markets",
                            SpeakerName = @"Ulrich HORST (Humboldt University Berlin)",
                            Location = "Extranef, room 126",
                            StartDate = new DateTime( 2014, 3, 4, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 13, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = "",
                            DetailsUrl = null
                        } },
                        { 63, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"IC Colloquium : Machine Learning for Social Systems: Modeling Opinions, Activities and Interactions",
                            SpeakerName = "",
                            Location = "BC 420",
                            StartDate = new DateTime( 2014, 3, 3, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 3, 17, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2732/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<strong>By </strong>Julian McAuley, Stanford University
<br /> IC Faculty candidate
<br />
<br />
<strong>Abstract</strong>
<br />
<br /> The proliferation of user-generated content on the web provides a wealth of opportunity to study humans through their online traces. I will discuss three aspects of my research, which aims to model and understand people's behavior online. First, I will develop rich models of opinions by combining structured data (such as ratings) with unstructured data (such as text). Second, I will describe how preferences and behavior evolve over time, in order to characterize the process by which people &quot;acquire tastes&quot; for products such as beer and wine. Finally, I will discuss how people organize their personal social networks into communities with common interests and interactions. These lines of research require models that are capable of handling high-dimensional, interdependent, and time-evolving data, in order to gain insights into how humans behave.
<br />
<br />
<strong>Bio</strong>
<br />
<br /> Julian McAuley is a postdoctoral scholar at Stanford University, where he works with Jure Leskovec on modeling the structure and dynamics of social networks. His current work is concerned with modeling opinions and behavior in online communities, especially with respect to their linguistic and temporal dimensions. Previously, Julian received his PhD from the ANU under Tiberio Caetano, with whom he worked on inference and learning in structured output spaces. His work has been featured in Time, Forbes, New Scientist, and Wired, and has received over 30,000 &quot;likes&quot; on Facebook.
<br />
<br />
<a href=""http://i.stanford.edu/~julian/"">More information</a>",
                            DetailsUrl = null
                        } },
                        { 64, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Specialized Macdonald polynomials, quantum K-theory, and Kirillov-Reshetikhin modules",
                            SpeakerName = @"Cristian Lenart, Max-Planck-Institut, Bonn, and State University of New York at Albany",
                            Location = "MAA331",
                            StartDate = new DateTime( 2014, 3, 3, 15, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 3, 17, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The (symmetric) Macdonald polynomials are Weyl group invariant polynomials with rational function coefficients in q,t, which specialize to the irreducible Lie algebra characters upon setting q=t=0. Quantum K-theory is a K-theoretic generalization of quantum cohomology. Kirillov-Reshetikhin (KR) modules are certain finite-dimensional modules for affine Lie algebras. Braverman and Finkelberg related the Macdonald polynomials specialized at t=0 to the quantum K-theory of flag varieties.&nbsp; With S. Naito, D. Sagaki, A. Schilling, and M. Shimozono, I proved that the same specialization of Macdonald polynomials equals the graded character of a tensor product of (one-column) KR modules. I will discuss the combinatorics underlying these connections.",
                            DetailsUrl = null
                        } },
                        { 65, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"High-Efficiency Computation in Embedded Systems",
                            SpeakerName = @"Dr. Phillip Stanley-Marbell, Apple Inc., Cupertino",
                            Location = "ME B1 B10",
                            StartDate = new DateTime( 2014, 3, 3, 15, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 3, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2787/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Computing systems embedded in our environments often operate under timing constraints and must conserve energy usage. Because they typically perform computation on noisy inputs, or generate outputs for perception by humans, high precision in their results is not always necessary.
<br />
<br /> This talk will address the research challenge of time-, energy-, and precision-efficient computation in embedded systems, with application to in-situ sensor data analytics. The talk will present theoretical results augmented by empirical data, to provide a deeper understanding of the tradeoff between time- and energy-efficiency through the use of parallelism, and how these tradeoffs are influenced by semiconductor-device and system-architectural properties. The insight gained from this analysis will be used to motivate two generations of embedded multiprocessor platforms developed to enable research in tradeoffs between energy-efficiency, performance, and tolerance to imprecision in computation and communication.
<br />
<br /> Bio:&nbsp;Phillip Stanley-Marbell received his Ph.D. from Carnegie Mellon University in 2007. He was a post-doctoral researcher at TU Eindhoven until 2008, when he joined IBM Research---Zurich as a permanent Research Staff Member. In 2012 he joined Apple to see his research ideas deployed in real-world products. Prior to completing his Ph.D., he held intern and full-time positions at AT&amp;T / Lucent Bell-Labs, Philips Consumer Communications, Lucent's Data Networking Group, and NEC Research Labs.&nbsp;
<br />
<br /> Dr. Stanley-Marbell is the author of a programming language textbook published by John Wiley &amp; Sons in 2003, and of over thirty scientific publications and seven patents / patent applications. He is a member of the ACM, IEEE, Sigma Xi, USENIX, and the Swiss Mathematical Society. From 2003--2004, he served as the copy editor for the ACM Mobile Computing and Communications Review journal.
<br />
<br /> His research interests are in architectures for high-efficiency embedded data processing, approximate compute architectures for future device technologies, and domain-specific programming languages for implementing precision-efficient algorithms.
<br />",
                            DetailsUrl = null
                        } },
                        { 66, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Nanoscale structuring and patterning of polymeric materials",
                            SpeakerName = @"Holger Schönherr, University of Siegen, Germany Bio: Holger Schönherr studied chemistry and polymer chemistry & physics at the Universities of Mainz and Toronto and finished his diploma thesis with Helmut Ringsdorf in 1995. He obtained his Ph.D. at the University of Twente, The Netherlands in 1999, working with G. Julius Vancso. Following a postdoctoral stay at Stanford University with Curtis W. Frank he joined the MESA+ Institute for Nanotechnology in Twente as assistant (later associate) professor before joining the University of Siegen in 2008 as a University Professor in Physical Chemistry. In April 2013 he was also appointed as guest professor at the Shanghai Jiaotong University. He was awarded, among others, with the Schloessmann award (Biology and Materials Science) of the Max Planck Society (1995), the DSM Award (2nd) for Chemistry & Technology (1999), a NWO vernieuwingsimpuls (VIDI) grant (2001), the Raphael-Eduard-Liesegang award of the German Colloid Society (2011), an ERC starting grant (2011), the POLYCHAR Materials Science Award 2013 and Research Prize of the Faculty of Science and Technology of the University of Siegen (2013). His research interests comprise the chemistry and physics of biointerfaces, self-assembled and nanostructured polymer systems, and surface analysis with atomic force microscopy. Currently running research projects: Polymer brushes and 3D  cell microenvironments, enzyme-labile block copolymer nanocapsules for the detection and treatment of bacterial wound infections, surface nanobubbles, nanostructured polymers via templating with anodic alumina, surface structuring with light-induced mass transport, and investigation of ligand-quadruplex DNA-interactions on structured surfaces.",
                            Location = "MXF1",
                            StartDate = new DateTime( 2014, 3, 3, 13, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 3, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2740/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<strong>Nanoscale structuring and patterning of polymeric materials to enhance functionality in “soft&quot; interfacial architectures</strong>
<br />
<br /> In this contribution I will highlight our recent efforts to impart functionality in “soft”, i.e. organic or polymeric interfacial architectures by means of controlling chemical patterns, topographic structures, shape and finally properties on micrometer to nanometer length scales.
<br />
<br /> The presentation will introduce in the first part the underlying concepts of nanopatterning and nanostructuring of polymers by placing established techniques and approaches as well as recently developed state of the art into proper context. Serial and parallel patterning techniques will receive attention and the advantages and limitations of selected examples of direct patterning methodologies, such as nanoimprint lithography, combined top-down - bottom-up approaches,
<em>1</em> and monolayer-based approaches like nanocontact printing
<em>2 </em>that are subsequently amplified by growing polymer brushes will be discussed.
<em>3</em>
<br />
<br /> In the second part our research into functional interfaces and the control of structure and properties on nanometer length scales will be addressed, in particular, template replication approaches for the fabrication of nanostructures, which exceed the size range of molecules and self-assembly approaches, by faithful 1 : 1 replication. In this context we have developed new approaches to shape pores formed during anodization of Al by temperature modulation.
<em>4</em> The resulting pores, which possess modulated diameters along their length, have been successfully replicated using e.g. layer-by-layer deposition of polyelectrolytes, resulting in novel, functional nanostructures, yielding the first example of complex structures by LBL that possess both concave and convex curvature.
<em>5,6 </em>In addition, polymer surface modification by means of reactive microcontact printing
<em>7</em> and scanning probe lithographic methods
<em>8</em> will be shown to provide access to highly controlled functional interfaces, e.g. to be applied in bacteria responsive coatings.
<em>9</em>
<br />
<br /> References
<br />
<br /> [1] T. M. Bl&auml;ttler, A. Binkert, M. Zimmermann, M. Textor, J. V&ouml;r&ouml;s, E. Reimhult Nanotechnology 2008, 19, 075301.
<br /> [2] B. R. Takulapalli, M. E. Morrison, J. Gu, P. Zhang Nanotechnology 2011, 22, 285302.
<br /> [3] R. Ducker, A. Garcia, J. Zhang, T. Chen, S. Zauscher Soft Matter, 2008, 4, 1774–1786.
<br /> [4] M. Raoufi, H. Sch&ouml;nherr RSC Advances 2013, 3, 13429.
<br /> [5] M. Raoufi, D. Tranchida, H. Sch&ouml;nherr Langmuir 2012, 28, 10091.
<br /> [6] M. Raoufi, H. Sch&ouml;nherr Langmuir 2014, in press.
<br /> [7] C. L. Feng, A. Embrechts, I. Bredebusch, J. Schnekenburger, W. Domschke, G. J. Vancso, H. Sch&ouml;nher, Advanced Materials 2007, 19, 286–290.
<br /> [8] Joost Duvigneau, Holger Sch&ouml;nherr, G. Julius Vancso ACS Applied Materials and Interfaces 2011, 3, 3855-65.
<br /> [9] Qi &amp; K.-S. T&uuml;cking, S. Handschuh-Wang, H. Sch&ouml;nherr Australian Journal of Chemistry 2014, in press.",
                            DetailsUrl = @"http://www.chemie-biologie.uni-siegen.de/pc/hs/index.html?lang=de"
                        } },
                        { 67, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Marches aléatoires sur les groupes : les premiers pas, I",
                            SpeakerName = @"Alex Monnard (EPFL)",
                            Location = "MA3 31",
                            StartDate = new DateTime( 2014, 3, 3, 11, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 3, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Group Theory Seminar",
                            DetailsUrl = @"http://ctg.epfl.ch/seminar"
                        } },
                        { 68, new EventItem
                        {
                            CategoryId = 1,
                            Name = @"Tropical Geometry and Topology",
                            SpeakerName = @"Grigory Mikhalkin ( University of Geneva)",
                            Location = "BI A0 448",
                            StartDate = new DateTime( 2014, 2, 21, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 5, 23, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2721/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Introduction of modern topological techniques in algebraic geometry based on the tropical limit. Developing a technique for recovering topology of a variety from its tropical limit.
<br />
<br /> 1. Tropical limit under different guises in Mathematics and Physics.
<br /> 2. Riemann surfaces and tropical curves.
<br /> 3. Tropical projective geometry, enumerative geometry.
<br /> 4. Correspondence theorems.
<br /> 5. Tropical homology theories and further topics.
<br />
<br /> Schedule on the website.",
                            DetailsUrl = @"http://cib.epfl.ch/PublicEvent.php?event=1034"
                        } },
                        { 69, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Friction feedback actuators using squeeze film effect",
                            SpeakerName = @"Christophe WINTER",
                            Location = "Auditoire Charles-Edouard Guillaume - Bâtiment Microcity - Rue de la Maladière 71b - 2000 Neuchâtel",
                            StartDate = new DateTime( 2014, 3, 28, 18, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 28, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : Prof. Y. Perriard
<br /> Systems and Robotics doctoral program.
<br /> Thesis 6128",
                            DetailsUrl = null
                        } },
                        { 70, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Theories of Experimentally Observed Excitation Spectra of Square Lattice Antiferromagnets",
                            SpeakerName = @"Bastien DALLA PIAZZA",
                            Location = "Auditoire CE4",
                            StartDate = new DateTime( 2014, 3, 28, 17, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 28, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis directors : Prof. H. M. R&oslash;nnow, Dr D. Ivanov
<br /> Physics doctoral program.
<br /> Thesis 6090",
                            DetailsUrl = null
                        } },
                        { 71, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Ionic Liquid Based Field Effect Studies on La2-xSrxCuO4 Films and Related Materials",
                            SpeakerName = @"Guy Joseph Günter DUBUIS",
                            Location = "Auditoire CE3",
                            StartDate = new DateTime( 2014, 3, 27, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 27, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis directors : Prof. D. Pavuna, Prof. I. Bozovic
<br /> Physics doctoral program.
<br /> Thesis 6133",
                            DetailsUrl = null
                        } },
                        { 72, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Electronic Interfaces for Carbon Nanotube Electromechanical Oscillators and Sensors",
                            SpeakerName = @"Christian KAUTH",
                            Location = "Auditoire ELA 1",
                            StartDate = new DateTime( 2014, 3, 21, 17, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 21, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis directors : Prof. M. Kayal, Dr M. Pastre
<br /> Microsystems and Microelectronics doctoral program.
<br /> Thesis 6127
<br />",
                            DetailsUrl = null
                        } },
                        { 73, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Hydrodynamics and nonlinear phenomena in polariton fluids.",
                            SpeakerName = @"Gabriele GROSSO",
                            Location = "Auditoire CE4",
                            StartDate = new DateTime( 2014, 3, 21, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 21, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : Prof. B. Deveaud-Pl&eacute;dran
<br /> Physics doctoral program.
<br /> Thesis 6117",
                            DetailsUrl = null
                        } },
                        { 74, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"On Voltage and Frequency Control in Multi-Area Power Systems Security",
                            SpeakerName = @"Omid ALIZADEH MOUSAVI",
                            Location = "Auditoire EL A2",
                            StartDate = new DateTime( 2014, 3, 21, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 21, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : Dr. S.-R. Cherkaoui
<br /> Electrical Engineering doctoral program.
<br /> Thesis 6053",
                            DetailsUrl = null
                        } },
                        { 75, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Fatigue behaviour of UHPFRC and R-UHPFRC - RC composite members",
                            SpeakerName = @"Tohru MAKITA",
                            Location = "Salle GC B3 30",
                            StartDate = new DateTime( 2014, 3, 20, 16, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 20, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : Prof. E. Br&uuml;hwiler
<br /> Structures doctoral program.
<br /> Thesis 6068",
                            DetailsUrl = null
                        } },
                        { 76, new EventItem
                        {
                             CategoryId = 2,
                            Name = @"Monte Carlo Modeling of Crystal Channeling at High Energies",
                            SpeakerName = @"Philippe Jean SCHOOFS",
                            Location = "CERN - Salle Dirac (40-S2-D01) - 1211 Genève 23",
                            StartDate = new DateTime( 2014, 3, 18, 17, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 18, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis directors : Prof. L. Rivkin, Dr F. Cerutti
<br /> Physics doctoral program.
<br /> Thesis 6064",
                            DetailsUrl = null
                        } },
                        { 77, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Decomposition optimization strategy for the design and operation of district energy systems",
                            SpeakerName = @"Samira FAZLOLLAHI",
                            Location = "Auditoire CM4",
                            StartDate = new DateTime( 2014, 3, 14, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 14, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : Prof. F. Mar&eacute;chal
<br /> Energy doctoral program.
<br /> Thesis 6130",
                            DetailsUrl = null
                        } },
                        { 78, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"High gradient proton linacs for medical applications",
                            SpeakerName = @"Alberto DEGIOVANNI",
                            Location = "CERN - auditoire Kjell Johnsen 30-7-018, 1211 Genève 23",
                            StartDate = new DateTime( 2014, 3, 14, 17, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 14, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis directors : Prof. L. Rivkin, Prof. U. Amaldi
<br /> Physics doctoral program.
<br /> Thesis 6069",
                            DetailsUrl = null
                        } },
                        { 79, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Near-Field Enhancement in Plasmonic Arrays",
                            SpeakerName = @"Thomas Gerd SIEGFRIED",
                            Location = "Paul Scherrer Institute - Building: SZ-OSGA Room: EG06 - 5232 Villigen-PSI",
                            StartDate = new DateTime( 2014, 3, 14, 15, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 14, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis directors : Prof. O. Martin, Dr. H. C. Sigg
<br /> Photonics doctoral program.
<br /> Thesis 6114
<br />",
                            DetailsUrl = null
                        } },
                        { 80, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Three Dimensional Microstructures for Cell Culture with Engineered Stiffness and Geometry",
                            SpeakerName = @"Mattia MARELLI",
                            Location = "Auditoire ELA 2",
                            StartDate = new DateTime( 2014, 3, 7, 18, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : Prof. J. Brugger
<br /> Microsystems and Microelectronics doctoral program
<br /> Thesis 6076",
                            DetailsUrl = null
                        } },
                        { 81, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Experimental and theoretical studies on the effects of smooth muscle on the mechanical response and remodeling of arteries",
                            SpeakerName = @"Aristotelis AGIANNIOTIS",
                            Location = "Auditoire CE4",
                            StartDate = new DateTime( 2014, 3, 7, 17, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : by Prof. N. Stergiopulos
<br /> Biotechnology and Bioengineering doctoral program.
<br /> Thesis 5793",
                            DetailsUrl = null
                        } },
                        { 82, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"On Multi-Dimensional Privacy in Context-Aware Mobile Networks",
                            SpeakerName = @"Igor BILOGREVIC",
                            Location = "Salle BC 420",
                            StartDate = new DateTime( 2014, 3, 7, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : Prof. J.-P. Hubaux
<br /> Computer, Communication and Information Sciences doctoral program.
<br /> Thesis 6066",
                            DetailsUrl = null
                        } },
                        { 83, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Objective Assessment of Swimming Biomechanics Using Wearable Inertial Sensors",
                            SpeakerName = @"Farzin DADASHI",
                            Location = "Auditoire EL A1",
                            StartDate = new DateTime( 2014, 3, 7, 17, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis director : Prof. K. Aminian
<br /> Electrical Engineering doctoral program
<br /> Thesis 6055",
                            DetailsUrl = null
                        } },
                        { 84, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Choix d'un label énergétique par les promoteurs immobiliers en France et en Suisse",
                            SpeakerName = @"Virginie SILBERSTEIN",
                            Location = "Auditoire MXF1",
                            StartDate = new DateTime( 2014, 3, 6, 17, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 6, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis directors : Prof. V. Kaufmann, Prof. Ph. Thalmann
<br /> Environment doctoral program.
<br /> Thesis 6103",
                            DetailsUrl = null
                        } },
                        { 85, new EventItem
                        {
                            CategoryId = 2,
                            Name = @"Quench tests of LHC magnets with beam: studies on beam loss development and determination of quench levels",
                            SpeakerName = @"Agnieszka PRIEBE",
                            Location = "Salle 503-1-001 Council Chamber, CERN, 1211 Genève 23",
                            StartDate = new DateTime( 2014, 3, 4, 16, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Thesis directors : Prof. M. Q. Tran, Dr M. Sapinski
<br /> Physics doctoral program.
<br /> Thesis 6116",
                            DetailsUrl = null
                        } },
                        { 86, new EventItem
                        {
                            CategoryId = 3,
                            Name = @"Learning DAHU – Contemporary Dance",
                            SpeakerName = @"Cie Philippe Saire",
                            Location = "Rolex Learning Center",
                            StartDate = new DateTime( 2014, 3, 26, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 27, 19, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2557/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Philippe Saire, a Lausanne-based contemporary dance choreographer, has created a performance exclusively for the Rolex Learning Center.
<br />
<br /> The idea is to physically integrate the specificities of the spectacular architecture of the venue into the performance and have the dancers play with the available topographies and the audience’s surprising vantage points.
<br />
<br />
<strong>Public rehearsals</strong> will be held in the Rolex Learning Center on Friday 21, Saturday 22, Monday 24, and Tuesday 25 March, between 10:00 a.m. and 6:00 p.m. A great opportunity to watch the dancers and their choreographer work in a public space!
<br />
<br />
<strong>The performances </strong>will be carried out on Wednesday 26 and Thursday 27 March, at 12:00 p.m. and 7:00 p.m.",
                            DetailsUrl = @"http://culture.epfl.ch/learning-dahu"
                        } },
                        { 87, new EventItem
                        {
                            CategoryId = 3,
                            Name = @"Spectacle ""Couvre-feux""",
                            SpeakerName = "",
                            Location = "Théâtre La Grange de Dorigny",
                            StartDate = new DateTime( 2014, 3, 13, 19, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 16, 21, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2806/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<br />
<strong>Par la Cie Jeanne F&ouml;hn</strong>
<br />
<strong>Texte : </strong>Didier-Georges Gabily
<br />
<strong>Mise en sc&egrave;ne et adaptation :</strong> Ludovic Chazaud
<br />
<strong>Jeu :</strong> Baptiste Gilli&eacute;ron, Aline Papin, Rosangela Gramoni
<br /> et, en alternance, Hannah Jones et Mathilde Liengme
<br />
<br />
<em>Un homme revient dans sa campagne d'origine pour l'enterrement de sa grand-m&egrave;re. Il vient avec sa fille. De ce retour aux terres originelles, se m&ecirc;lent les envies du temps pr&eacute;sent, les frayeurs d'antan.<br /> C'est un voyage dans les temps que nous propose Didier-Georges Gabily, dans une langue formidablement belle. Une histoire d'amour et de d&eacute;sirs d'un p&egrave;re pour sa grand-m&egrave;re d&eacute;c&eacute;d&eacute;e et sa fille bien vivante, un trouble entre fantasme et r&eacute;alit&eacute;, une fuite en avant vers le souvenir. Le texte est construit comme si la t&ecirc;te ne pouvait penser qu'&agrave; une seule chose &agrave; la fois, en la figeant unanimement. C'est un regard distanci&eacute; au pr&eacute;sent sur une aventure pass&eacute;e... On y trouve tout ce qui fait trace dans l'oreille d'un enfant, tout ce qui fait trace dans l'oeil d'un p&egrave;re, tout ce qui fait trace dans le coeur d'un &ecirc;tre qui quitte et abandonne.</em>
<br />
<br />
<strong>Autour du spectacle</strong>
<br />
<strong>Jeudi 13 mars 2014 &agrave; l'issue de la repr&eacute;sentation :</strong> discussion informelle autour du texte de Didier-Georges Gabily, en collaboration avec la section de fran&ccedil;ais de l'UNIL. Avec Roxane Cherubini (&eacute;tudiante Master), Delphine Abrecht (assistante dipl&ocirc;m&eacute;e), Lise Michel (professeure assistante), Ludovic Chazaud, l'&eacute;quipe de cr&eacute;ation et le public !
<br />
<br />
<strong>Infos pratiques<br /> Horaires :</strong> je-sa 19h / ve 20h30 / di 17h
<br />
<strong>Tarifs :</strong> plein 20.- / collab. UNIL+EPFL 15.- / &eacute;tudiant 10.-
<br />
<strong>R&eacute;servations :</strong> par r&eacute;pondeur au 021 692 21 24 ou 
<a href=""http://www3.unil.ch/wpmu/grangededorigny/2013/06/couvre-feux/"">en ligne</a>
<br /> Le bar et la billetterie ouvrent une heure avant le d&eacute;but de la repr&eacute;sentation et les billets se retirent sur place.",
                            DetailsUrl = @"http://www3.unil.ch/wpmu/grangededorigny/2013/06/couvre-feux/"
                        } },
                        { 88, new EventItem
                        {
                            CategoryId = 3,
                            Name = @"Midi_Classics – Unil-EPFL Choir",
                            SpeakerName = @"Lausanne University Choir",
                            Location = "Salle Polyvalente",
                            StartDate = new DateTime( 2014, 3, 11, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 11, 13, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2599/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"During a musical lunch break, the Lausanne University Choir will interpret Missa Brevis by Kod&aacute;ly and the Requiem by Durufl&eacute;, under the direction of Fruzsina Szuromi.",
                            DetailsUrl = @"http://culture.epfl.ch/Midi_Classics-choeur-unil-epfl"
                        } },
                        { 89, new EventItem
                        {
                            CategoryId = 3,
                            Name = @"Brain Awareness Week Lausanne",
                            SpeakerName = "",
                            Location = "CHUV - Auditoire César-Roux",
                            StartDate = new DateTime( 2014, 3, 10, 18, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 13, 20, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2565/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<strong>Public talks / Exhibition at 'Mus&eacute;e de la Main' / Movie night at 'casino de Montbenon'</strong>
<br />
<br /> Find all the info in the attached program or on the event website: 
<a href=""http://www.lasemaineducerveau.ch"">lasemaineducerveau.ch</a>",
                            DetailsUrl = null
                        } },
                        { 90, new EventItem
                        {
                            CategoryId = 3,
                            Name = @"Spectacle ""Yvonne, princesse de Bourgogne""",
                            SpeakerName = "",
                            Location = "Théâtre La Grange de Dorigny",
                            StartDate = new DateTime( 2014, 2, 27, 19, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 8, 22, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2749/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Par l'ascenseur &agrave; poissons / cie
<br />
<strong>Texte :</strong> Witold Gombrowicz
<br />
<strong>Mise en sc&egrave;ne :</strong> Genevi&egrave;ve Guhl
<br />
<strong>Jeu :</strong> Elidan Arzoni, Julia Batinova, Greta Gratos, Genevi&egrave;ve Guhl, Jo&euml;l Hefti, Ilil Land-Boss, Jos&eacute; Lillo, Fr&eacute;d&eacute;ric Lugon, Olivia Seigne
<br />
<strong>Musiciens et compositions originales :</strong> Christian Pralong (guitare, chant), G&eacute;raldine Schenkel (piano, accord&eacute;on, bandon&eacute;on, h&eacute;licon)
<br />
<br />
<em>Farce grotesque, Yvonne, princesse de Bourgogne est une sorte de parodie shakespearienne. H&eacute;ritier du tr&ocirc;ne, le prince Philippe projette d’&eacute;pouser par d&eacute;fi des conventions, Yvonne, une fille d&eacute;pourvue de charme, insignifiante et ennuyeuse. L’arriv&eacute;e de cette princesse &laquo; hors norme &raquo; va bouleverser les codes et les valeurs de la cour. Par sa seule pr&eacute;sence, elle &eacute;veille au sein du royaume, agressivit&eacute; et haine en mettant &agrave; nu les failles et les vices de tout en chacun. M&ecirc;me l’&eacute;lite tremble, personne n’est plus &agrave; l’abri de &laquo; l’effet &raquo; Yvonne….<br /> &Eacute;crite en 1938, Yvonne, princesse de Bourgogne est la premi&egrave;re pi&egrave;ce de Gombrowicz. Comme un geste de r&eacute;bellion, joyeux et vivifiant, qui revendique son immaturit&eacute; et sa jeunesse, elle se joue des formes, flirte avec plusieurs styles, le clown, l’absurde, la com&eacute;die de mœurs, la trag&eacute;die.</em>
<br />
<br />
<strong>Infos pratiques</strong>
<br />
<strong>Horaires : </strong>ma-je-sa 19h / me-ve 20h30 / di 17h
<br />
<strong>Tarifs :</strong> plein 20.- / collab. UNIL+EPFL 15.- / &eacute;tudiant 10.-
<br />
<strong>R&eacute;servations :</strong> par r&eacute;pondeur au 021 692 21 24 ou 
<a href=""http://www3.unil.ch/wpmu/grangededorigny/2013/06/yvonne-princesse-de-bourgogne/"">en ligne</a>
<br /> Le bar et la billetterie ouvrent une heure avant le d&eacute;but de la repr&eacute;sentation et les billets se retirent sur place.
<br />",
                            DetailsUrl = @"http://www3.unil.ch/wpmu/grangededorigny/2013/06/yvonne-princesse-de-bourgogne/"
                        } },
                        { 91, new EventItem
                        {
                            CategoryId = 3,
                            Name = @"ERBRA - an artistic performance by Anne Rochat",
                            SpeakerName = @"Anne Rochat, artist",
                            Location = "Rolex Learning Center - Patios extérieurs",
                            StartDate = new DateTime( 2013, 10, 1, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 6, 30, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2465/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Throughout the 2013-2014 academic year, artist 
<a href=""http://annerochat.tumblr.com/"">Anne Rochat</a> presents her art on EPFL’s campus. A hybrid orchard is created through the fusion of different kinds of woods. Potted, it becomes a nomad. Artist Anne Rochat will display her orchard like a sculpture in some of the RLC’s patios. Twice, she will stage a performance with the trees, which will be both a decorative prop and an actor. Anne Rochat’s performances will invite you to marvel.
<br />
<br />
<strong>Performances with the artist:</strong> 2nd of October 2013 and 1st of May 2014",
                            DetailsUrl = @"http://culture.epfl.ch/ERBRA"
                        } },
                        { 92, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Discrete Choice Analysis: Predicting Demand and Market Shares",
                            SpeakerName = @"Prof. Michel Bierlaire, EPFL ENAC INTER TRANSP-OR; Prof. Moshe Ben-Akiva, MIT, Cambridge, USA; Prof. Daniel McFadden, University of California, Berkeley, USA (Nobel Prize Laureate, 2000); Prof. Joan Walker, University of California, Berkeley, USA.",
                            Location = "QIE 0108",
                            StartDate = new DateTime( 2014, 3, 23, 8, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 27, 17, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The course is designed for professionals (from industry and public authorities) and academic researchers (professors, researchers, PhD students), interested in understanding and predicting consumer choices, demand and market share, such as marketing analysts, managers, planners, economists, engineers, operations researchers",
                            DetailsUrl = @"http://transp-or.epfl.ch/dca/index.php"
                        } },
                        { 93, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Journée sans viande",
                            SpeakerName = "",
                            Location = "Toutes les cafétérias et restaurants de l'EPFL et de l'UNIL",
                            StartDate = new DateTime( 2014, 3, 20, 11, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 20, 0, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2793/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"A l'occasion de la Journ&eacute;e internationale sans viande, Unipoly organise le 20 mars, conjointement avec l'UNIL et l'EPFL une journ&eacute;e sans viande dans toutes les caf&eacute;t&eacute;rias et restaurants du campus. Les restaurateurs se sont engag&eacute;s &agrave; exclure la viande des plats servis ce jour-l&agrave;.
<br /> L'objectif de cette journ&eacute;e est de sensibiliser les &eacute;tudiants et les collaborateurs de l'UNIL et de l'EPFL aux impacts environnementaux li&eacute;s &agrave; la consommation de viande.",
                            DetailsUrl = @"https://unipoly.epfl.ch/public/projets/journee_sans_viande"
                        } },
                        { 94, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Journée sans viande",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 3, 20, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 20, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2789/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"A l'occasion de la Journ&eacute;e internationale sans viande, Unipoly organise le 20 mars, conjointement avec l'Unil et l'EPFL une journ&eacute;e sans viande dans toutes les caf&eacute;t&eacute;rias et restaurants du campus. Les restaurateurs se sont engag&eacute;s &agrave; exclure la viande des plats servis ce jour-l&agrave;.
<br /> L'objectif de cette journ&eacute;e est de sensibiliser les &eacute;tudiants et les collaborateurs de l'Unil et de l'EPFL aux impacts environnementaux li&eacute;s &agrave; la consommation de viande.",
                            DetailsUrl = @"https://unipoly.epfl.ch/public/projets/journee_sans_viande"
                        } },
                        { 95, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Démon Robopoly, invité: Jean-Daniel Nicoud",
                            SpeakerName = @"Jean-Daniel Nicoud",
                            Location = "ELA2",
                            StartDate = new DateTime( 2014, 3, 17, 12, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 17, 13, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Pour ce d&eacute;mon, le club de robotique accueil un invit&eacute;, qui nous pr&eacute;sentera les aspects d'un projet robotique.
<br />
<br /> Les robots mobiles ont peu &eacute;volu&eacute;s en 20 ans car la loi de Moore ne s'applique pas aux capteurs et aux sources d'&eacute;nergie. Les projets sont plus facile actuellement, et un peu plus complexes, ce qui force une bonne approche modulaire pour le mat&eacute;riel et le logiciel.
<br /> La pr&eacute;sentation commentera des robots p&eacute;dagogiques anciens et r&eacute;cents et analysera quelques difficult&eacute;s rencontr&eacute;es dans les projets.",
                            DetailsUrl = @"http://robopoly.epfl.ch/"
                        } },
                        { 96, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"1st Symposium of the Pôle Thématique de Recherche (PTR) Métabolisme-Nutrition-Vieillissement LIMNA Lausanne Integrative Metabolism and Nutrition Alliance",
                            SpeakerName = @"Speakers: (SEE ENCLOSED PROGRAM) Prof. Graham Hardie, University of Dundee, Scotland Prof. Philippe Froguel, University Lille 2, France Prof. Johan Auwerx - EPFL Prof. Nelly Pitteloup - CHUV Prof. David Gatfield – CIG, UNIL Prof. Lluis Fajas Coll – DP, UNIL Prof. Luc Tappy – DP, UNIL Prof. Beatrice Desvergne – CIG, UNIL Prof. Bernard Thorens – CIG, UNIL",
                            Location = "Casino de Montreux",
                            StartDate = new DateTime( 2014, 3, 17, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 17, 18, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"1st Symposium of the P&ocirc;le Th&eacute;matique de Recherche (PTR) M&eacute;tabolisme-Nutrition-Vieillissement
<br /> LIMNA (Lausanne Integrative Metabolism and Nutrition Alliance)",
                            DetailsUrl = null
                        } },
                        { 97, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Conférence avec Fabrice Hadjadj, philosophe et écrivain à succès",
                            SpeakerName = @"Fabrice Hadjadj, philosophe et écrivain d'origine juive, de nom arabe, catholique et français.",
                            Location = "auditoire CO1",
                            StartDate = new DateTime( 2014, 3, 13, 18, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 13, 20, 15, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2802/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"&quot;Peut-on encore parler de Dieu&quot; ?
<br /> Fabrice Hadjadj se penchera sur la question de Dieu dans une soci&eacute;t&eacute; laique o&ugrave; la religion est rel&eacute;gu&eacute;e &agrave; la sph&egrave;re intime ou d&eacute;cri&eacute;e dans une morale publique.
<br />
<br /> Quelle est r&eacute;ellement la place de Dieu s'il y en a une encore? Au-del&agrave; des fondamentalismes et de la morale comment arriver &agrave; une civilisation de la paix et du respect de la diff&eacute;rence?
<br />",
                            DetailsUrl = @"http://aumonerie.epfl.ch/"
                        } },
                        { 98, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Journées d'information",
                            SpeakerName = "",
                            Location = "Forum Rolex Learning Center",
                            StartDate = new DateTime( 2014, 3, 13, 9, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 14, 16, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2769/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"L'EPFL organise ses traditionnelles journ&eacute;es d'information pour les gymnasiens, jeudi et vendredi 6&amp;7 mars, et jeudi et vendredi 13&amp;14 mars.
<br /> Les jeudis, des s&eacute;ances d'information permettront aux gymnasiens de d&eacute;couvrir les diff&eacute;rentes sections d'&eacute;tudes de l'EPFL, d'obtenir des renseignements sur les aspects pratiques (logement, inscription, etc), ainsi que de visiter le campus.
<br /> Les vendredis, les gymnasiens pourront participer &agrave; un stage ou une visite plus pouss&eacute;e dans la section de leur choix.",
                            DetailsUrl = @"http://bachelor.epfl.ch/journees-info"
                        } },
                        { 99, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Des labos et TP : pour acquérir quelles compétences?",
                            SpeakerName = @"Jean-Louis Ricci Ingrid Le Duc",
                            Location = "salle BI A2 468",
                            StartDate = new DateTime( 2014, 3, 11, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 11, 12, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/1369/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"This workshop reviews different types of laboratory and practicals (TP), assisting you in clarifying and defining appropriate learning outcomes.",
                            DetailsUrl = @"https://docs.google.com/document/d/1JLAcz5JP1KDh7YoOIYXjE0KxPYIABzgYfK679-zzKCg/edit"
                        } },
                        { 100, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"UNIPOLY Recrute!",
                            SpeakerName = "",
                            Location = "CM 1221",
                            StartDate = new DateTime( 2014, 3, 10, 12, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 10, 13, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2792/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"S&eacute;ance d'information et pr&eacute;sentation de l'association, de nos activit&eacute;s etc.",
                            DetailsUrl = @"https://unipoly.epfl.ch//start"
                        } },
                        { 101, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"PUBLICA, les changements et mesures d'accompagnement",
                            SpeakerName = @"Albert Meyer, DRH, responsable salaires et prévoyance",
                            Location = "EPFL, SG1",
                            StartDate = new DateTime( 2014, 3, 7, 17, 15, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 18, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"D&egrave;s le 1er janvier 2015, l'adaptation du taux technique entra&icirc;ne une baisse significative du taux de conversion, donc nos prestations vieillesse.
<br /> Le responsable salaires et pr&eacute;voyance &agrave; l'EPFL, Albert Meyer, vient expliquer cette m&eacute;canique et les mesures d'accompagnement prises et r&eacute;ponde &agrave; vos questions.
<br /> Seront &eacute;galement pr&eacute;sents pour discussion : Philippe Thalmann, membre de la Commission de Caisse Publica (repr&eacute;sentant du domaine des EPF), MM. Robert Dalang et Xavier Llobet, membres de la commission paritaire EPFL.",
                            DetailsUrl = @"http://rh.epfl.ch/page-40803.html"
                        } },
                        { 102, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Journées d'information",
                            SpeakerName = "",
                            Location = "Forum Rolex Learning Center",
                            StartDate = new DateTime( 2014, 3, 6, 9, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 7, 16, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2768/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"L'EPFL organise ses traditionnelles journ&eacute;es d'information pour les gymnasiens, jeudi et vendredi 6&amp;7 mars, et jeudi et vendredi 13&amp;14 mars.
<br /> Les jeudis, des s&eacute;ances d'information permettront aux gymnasiens de d&eacute;couvrir les diff&eacute;rentes sections d'&eacute;tudes de l'EPFL, d'obtenir des renseignements sur les aspects pratiques (logement, inscription, etc), ainsi que de visiter le campus.
<br /> Les vendredis, les gymnasiens pourront participer &agrave; un stage ou une visite plus pouss&eacute;e dans la section de leur choix.
<br />",
                            DetailsUrl = @"http://bachelor.epfl.ch/journees-info"
                        } },
                        { 103, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Specialized Masters Day",
                            SpeakerName = "",
                            Location = "Hall SG, SG0211 and SG0213",
                            StartDate = new DateTime( 2014, 3, 4, 12, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 18, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2664/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"EPFL organizes on March 4th an information day to present its specialized Master's programs. These Masters have been developed in fields where a particular demand for professionals exists on the job market and offer a challenging alternative to students willing to follow a transdisciplinary program, strongly linked with economy and industry.
<br />
<br /> From 12:00 to 18:00, presentations and information stands will give you the opportunity to get more information and meet staff from the following programs :
<br />
<br /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - 
<a href=""http://master.epfl.ch/cse"">Computational science and engineering</a>
<br /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - 
<a href=""http://master.epfl.ch/energy"">Energy management and sustainability</a>
<br /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - 
<a href=""http://master.epfl.ch/financial"">Financial engineering</a>
<br /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - 
<a href=""http://master.epfl.ch/management"">Management, technology and entrepreneurship</a>
<br /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; - 
<a href=""http://master.epfl.ch/nuclearengineering"">Nuclear engineering</a>
<br />
<br /> At 6 pm, an aperitive will follow the manifestation. Registration is mandatory.
<br />
<br />
<a href=""http://master.epfl.ch/page-103766-en.html"">Program and registration</a>",
                            DetailsUrl = null
                        } },
                        { 104, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Effective Assessment",
                            SpeakerName = @"Roland Tormey",
                            Location = "salle BI A2 458",
                            StartDate = new DateTime( 2014, 3, 4, 9, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 4, 12, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/1263/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"To develop assessment techniques which are valid and objective, notably to test if students have met the required learning outcomes.",
                            DetailsUrl = @"https://docs.google.com/document/d/15UZwLFBMcgndeDxHbydSC8VfORtp1fY7p3y7N0g-a5w/edit"
                        } },
                        { 105, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"""From Farm to Fork"" Fusebox Challenge Kick Off by Bühler",
                            SpeakerName = @"Ian Roberts, CTO Bühler Group",
                            Location = "Rolex Learning Center Espace Hodler",
                            StartDate = new DateTime( 2014, 3, 3, 18, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 3, 19, 30, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2783/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"More than 30% of the food produced is lost. How can we improve the current food value chain to feed 8 billion people by 2025?
<br /> Ian Roberts, Chief Technology Officer at B&uuml;hler Group will introduce the key challenges of the current food value chain and provide you with some key insights to stir your creativity for&nbsp;the upcoming Fusebox&nbsp;challenge that will take place from March 4th-March 21st on fusebox.epfl.ch.A networking cocktail will be offered after the conference.",
                            DetailsUrl = null
                        } },
                        { 106, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Take the challenge and launch your startup !",
                            SpeakerName = @"A different field expert every week will share his/her entrepreneurial knowledge and exeprience.",
                            Location = "Odyssea",
                            StartDate = new DateTime( 2014, 2, 18, 17, 0, 0 ),
                            EndDate = new DateTime( 2014, 5, 27, 21, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2668/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Register to the venture challenge!
<br />
<br /> Unleash your entrepreneurial skills, launch your startup!
<br />
<br /> Are you a student, doctoral student, postdoc or graduate from the EPFL, UNIL or another Swiss university? Do you have an entrepreneurial project? Do you intend to launch your own company or work in a startup?
<br />
<br /> Take part in our &quot;venture challenge&quot; program starting February 18th at EPFL, and benefit free of charge from compact practical knowledge and many tips and tricks to run your business from leading startup-experts: business strategy, marketing, communication, HR... This course is restricted to 25 participants only.
<br />
<br /> This venture challenge is organized by venturelab and supported by the CTI training program “Start-Up and Entrepreneurship”. The course will be taught in English, each Tuesday, from 5 to 9pm at EPFL&nbsp; in the Odyssea building.
<br />
<br /> Are you interested? Please do not hesitate to get information and apply on our website (http://www.venturelab.ch/venturechallenge). Preference will be given to candidates who apply first and who have a concrete entrepreneurial project. Deadline : 15th of february.
<br />
<br />
<br />",
                            DetailsUrl = @"http://venturelab.ch/index.cfm?page=130298"
                        } },
                        { 107, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"The Open Education Challenge: Funding Opportunity for Education Start-ups in Europe",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 2, 12, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 17, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2715/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Are you an innovative student, teacher or professor? Do you want to share your innovative practices and have a greater impact in schools and universities all over Europe and beyond? The 
<strong><a href=""http://openeducationchallenge.eu/"">Open Education Challenge</a></strong> – launched in partnership with the European Commission - is your opportunity to transform your idea into a successful company and change the way we teach and learn.
<br />
<br /> The Open Education Challenge invites innovators to submit their proposal by 
<strong>17 March 2014</strong>. Twenty finalists will get a chance to go to Barcelona to pitch their idea, and the 10 winners will participate in the 
<strong>12-week incubator</strong>. The intensive coaching sessions will take place in 
<strong>five successive cities; Barcelona, Paris, London, Berlin, and Helsinki</strong>. Each team will be mentored by an expert from the field of education, entrepreneurship, or technology. The winners will have 
<strong>access to up to €20.000 in seed capital</strong>, and at the end of the incubator they will present their projects to the Open Education Investment Club with the possibility to secure additional financing.
<br />
<br /> Discover why the Open Education Challenge is made for you. 
<strong>Join our informal meeting about the Open Education Challenge on Wednesday 12 February 2014 at 14h00 at the EPFL Campus, Room CM 1 221</strong> and visit 
<a href=""http://www.openeducationchallenge.eu/blog?page=1"">our blog</a>, follow us on 
<a href=""http://www.facebook.com/openeducationchallenge"">Facebook</a> and 
<a href=""http://twitter.com/OpenEdChallenge"">Twitter</a>.",
                            DetailsUrl = null
                        } },
                        { 108, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"La Sélection de Vin des étudiants de génie civil - événement prolongé",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2013, 11, 4, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 28, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/1850/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"<strong>C'est avec le plus grand des plaisirs...</strong>
<br />
<br /> que le comit&eacute; de Travel GC a d&eacute;cid&eacute; de prolonger l'&eacute;v&eacute;nement.
<br /> Il vous est donc encore possible, &agrave; vous qui avez oubli&eacute; de le faire,
<br /> de choisir parmis notre s&eacute;lection de mill&eacute;simes,
<br /> lequel ornementra votre prochaine r&eacute;ception...
<br />
<br /> Nous esp&eacute;rons que vous vous d&eacute;lecterez autant que nous
<br /> lors de la d&eacute;gustation de ces nectars...
<br />
<br /> ...n'h&eacute;sitez &agrave; venir faire un tour sur le site web
<br /> et &agrave; passer comande &agrave; l'un d'entre nous
<br /> ou directement &agrave; 
<a href=""mailto:travelgc@epfl.ch"">travelgc@epfl.ch</a>
<br />
<br />
<br /> le comit&eacute; Travel GC
<br />
<br />
<br />",
                            DetailsUrl = @"http://aegc.epfl.ch/page-95643.html"
                        } },
                        { 109, new EventItem
                        {
                            CategoryId = 4,
                            Name = @"Certificate of Advanced Studies in Management of Development Projects",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2013, 9, 30, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 6, 15, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"The Cooperation &amp; Development Center (CODEV) at&nbsp;EPFL is pleased to announce the 
<strong>Call for Applications for the Certificate of Advanced Studies in Management of Development Projects (MaDePro)</strong>.
<br />
<br /> This program proposes an integrated and interdisciplinary approach and targets professionals in both Northern and Southern countries who wish to pursue a career in development or international cooperation and/or individuals from government, NGOs, IOs, IGOs, or the private sector:
<br />
<br /> i) who want to broaden their understanding of development issues;
<br /> ii) acquire tools to assess and manage development projects;
<br /> iii) combine important elements of development and management.
<br />
<br /> The program is based on the 
<strong>North-South scientific partnership</strong>. It gives participants an opportunity to improve their knowledge in such aspects as Development, Technology and Project Management directly linked to the reality and actual experience of the field in such a “heterogeneous” country as India where poverty co-exists alongside vast scientific expertise and local know-how. The training provides a chance to share the different standpoints and experiences of Europe and Asia.
<br />
<br /> The program is structured in three parts, all which are compulsory. 
<strong>Part 1</strong> (30 September - 2 March 2014) involves 20 weeks of 
<strong>e-Learning</strong> covering such aspects as: International Environment, Sustainable Development, Main Development Issues, Technology for Development, Entrepreneurship and Innovation, Project Management. 
<strong>Part 2</strong> (17-30 March 2014), is 
<strong>full time</strong>, lasts 2 weeks and will be held in 
<strong>India</strong> and will focus on field activities. 
<strong>Part 3 </strong>(31 March – 15 June 2014), consists of an 
<strong>individual project</strong>.
<br />
<br /> The program uses a wide range of tools including: e-Learning, lectures, field trips, case studies, group work, individual project, etc.
<br />
<br /> Applicants must have a university degree and very good English.
<br />
<br /> Full and/or partial grants may be offered to participants from developing and emerging countries that are not able to meet the total costs involved.&nbsp; Note that grants either full or partial will not cover the cost of travel to/from India.
<br />
<br /> For full information and application procedure refer to our website 
<a href=""http://cooperation.epfl.ch/madepro""><strong>http://cooperation.epfl.ch/madepro</strong></a>.
<br />
<br /> Only applications following the instructions detailed and submitted via the online submission system on the program webpage will be accepted.&nbsp; Incomplete applications will not be considered.
<br />
<br /> The 
<strong>closing date for receipt of applications is 31 July 2013</strong>, by midnight local Swiss time.
<br />",
                            DetailsUrl = @"http://cooperation.epfl.ch/madepro"
                        } },
                        { 110, new EventItem
                        {
                            CategoryId = 5,
                            Name = @"Study trip",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 3, 12, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 12, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"One day, for following classes : 2nd year Bachelor and 1st year Master of all Sections",
                            DetailsUrl = null
                        } },
                        { 111, new EventItem
                        {
                            CategoryId = 5,
                            Name = @"Study trip",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 3, 12, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 12, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"One day, for following classes : 1st and 2nd year Bachelor and 1st year Master of all Sections",
                            DetailsUrl = null
                        } },
                        { 112, new EventItem
                        {
                            CategoryId = 5,
                            Name = @"Study trip",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 3, 12, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 12, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"One day, for following classes : 1st and 2nd year Bachelor and 1st year Master of all Sections",
                            DetailsUrl = null
                        } },
                        { 113, new EventItem
                        {
                            CategoryId = 5,
                            Name = @"Checking of enrolment to Master project",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 3, 10, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 10, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"deadline",
                            DetailsUrl = null
                        } },
                        { 114, new EventItem
                        {
                            CategoryId = 5,
                            Name = @"Checking of enrolment to Master project",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 3, 10, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 10, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"deadline",
                            DetailsUrl = null
                        } },
                        { 115, new EventItem
                        {
                            CategoryId = 5,
                            Name = @"Enrolment for Masters projects titles checking",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 3, 3, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 3, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"deadline",
                            DetailsUrl = null
                        } },
                        { 116, new EventItem
                        {
                            CategoryId = 5,
                            Name = @"Enrolment for Masters projects titles checking",
                            SpeakerName = "",
                            Location = "",
                            StartDate = new DateTime( 2014, 3, 3, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 3, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = "",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"deadline",
                            DetailsUrl = null
                        } },
                        { 117, new EventItem
                        {
                            CategoryId = 6,
                            Name = @"PATRICK BERGER - Architecte, Paris",
                            SpeakerName = "",
                            Location = "Espace Archizoom",
                            StartDate = new DateTime( 2014, 3, 6, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 5, 8, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2616/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Du 6 mars au 8 mai 2014, l’espace Archizoom &agrave; l’Ecole polytechnique f&eacute;d&eacute;rale de Lausanne donne carte blanche &agrave; Patrick Berger &agrave; travers l’exposition et le livre qui l’accompagne. L’occasion de (re)d&eacute;couvrir les recherches que l’architecte parisien a men&eacute;es au cours de ses 20 ann&eacute;es d’enseignement en tant que professeur &agrave; l’EPFL.",
                            DetailsUrl = @"http://archizoom.epfl.ch/exposition"
                        } },
                        { 118, new EventItem
                        {
                            CategoryId = 6,
                            Name = @"ICE - a Journey to the Land of Icebergs",
                            SpeakerName = @"Robert Bolognesi & EPFL's Cultural and Arts Affairs",
                            Location = "Rolex Learning Center",
                            StartDate = new DateTime( 2014, 2, 19, 0, 0, 0 ),
                            EndDate = new DateTime( 2014, 3, 27, 0, 0, 0 ),
                            IsFullDay = true,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2555/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"A photo exhibit presenting pictures of the northern hemisphere’s most impressive icebergs, taken in the polar twilight by snow-scientist Robert Bolognesi.
<br />
<br />
<strong>Robert Bolognesi</strong> has been studying snow and avalanches since 1981. He earned his doctorate in EPFL’s Artificial Intelligence Laboratory (LIA). In 1992, he was hired as a researcher by the Federal Institute for Snow and Avalanche Research in Davos. In 1999, he founded METEORISK, where he continues to pursue his scientific work. He takes photographs both out of necessity, for his research, and out of passion.
<br />
<br /> Robert Bolognesi and Martin Beniston will join us for a debate at the next edition of 
<a href=""http://actu.epfl.ch/news/melting-ice-a-sublime-drama/""><strong>Science! on tourne &quot;Melting ice - a sublime drama&quot;: on Wednesday, March 12, 2014, at 12:15 p.m., at the Caf&eacute; Klee in the Rolex Learning Center</strong></a>",
                            DetailsUrl = @"http://culture.epfl.ch/ICE_en"
                        } },
                        { 119, new EventItem
                        {
                            CategoryId = 7,
                            Name = @"Animal ? Itinéraire d'une intuition",
                            SpeakerName = @"Patrick Berger, architecte, Paris",
                            Location = "Auditoire SG1",
                            StartDate = new DateTime( 2014, 3, 5, 17, 30, 0 ),
                            EndDate = new DateTime( 2014, 3, 5, 19, 0, 0 ),
                            IsFullDay = false,
                            TimeOverride = null,
                            ShortDetails = null,
                            PictureUrl = null,
                            ThumbnailUrl = @"http://memento.epfl.ch/image/2611/100x100.jpg",
                            HideThumbnail = false,
                            HideName = false,
                            HideInformation = false,
                            Details = @"Le&ccedil;on d'honneur et vernissage de l'exposition &agrave; Archizoom",
                            DetailsUrl = @"http://archizoom.epfl.ch/op/edit/patrick_berger"
                        } }
                    }
                }
            );
        }

        public Task<FavoriteEmailResponse> SendFavoriteItemsByEmailAsync( FavoriteEmailRequest request )
        {
            return Task.FromResult
            (
                new FavoriteEmailResponse
                {
                    Status = EventsStatus.Success
                }
            );
        }
    }
}
#endif