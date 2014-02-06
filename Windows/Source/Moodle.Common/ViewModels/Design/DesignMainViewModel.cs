// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Moodle.Models;

// Design data for MainViewModel

namespace PocketCampus.Moodle.ViewModels.Design
{
    public sealed class DesignMainViewModel
    {
#if DEBUG
        public bool IsLoading { get { return false; } }
        public bool HasError { get { return false; } }
        public bool HasNetworkError { get { return false; } }

        public bool AnyCourses { get { return true; } }

        public Course[] Courses
        {
            get
            {
                return new[]
                {
                    new Course
                    {
                        Name = "Algorithms",
                        Sections = new[]
                        {
                            new CourseSection
                            {
                                Name = "20 septembre - 26 septembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 1.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1540930/mod_resource/content/0/Lecture1.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Chapter 3 of book to refresh asymptotics.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1540933/mod_resource/content/0/chapter%203.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercises for session 1.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1541463/mod_resource/content/0/exercise1.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 2.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1541717/mod_resource/content/1/Lecture2.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions of exercises (session 1).pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1541738/mod_resource/content/0/exercise1-sol.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "27 septembre - 3 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 3.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1542725/mod_resource/content/2/Lecture3.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercise for session 2.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543019/mod_resource/content/1/exercise2.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "First Quiz with solutions .pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543020/mod_resource/content/1/quiz.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 4.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543063/mod_resource/content/1/Lecture4.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions of exercises (Session 2).pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543329/mod_resource/content/2/exercise2-sol.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "4 octobre - 10 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 5.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543833/mod_resource/content/1/Lecture5.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercises for Session 3.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543858/mod_resource/content/2/exercise3.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 6.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543951/mod_resource/content/2/Lecture6.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions of exercises (Session 3).pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544117/mod_resource/content/2/exercise3-sol.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "11 octobre - 17 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 7.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544777/mod_resource/content/2/Lecture7.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercises for Session 4.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544790/mod_resource/content/1/exercise4.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 8.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545005/mod_resource/content/2/Lecture8.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Second quiz with solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545219/mod_resource/content/2/quizfr.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions of exercises (Session 4).pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545221/mod_resource/content/2/exercise4-sol.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "18 octobre - 24 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 9.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545837/mod_resource/content/2/Lecture9.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercises for Session 5.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545839/mod_resource/content/2/exercise5.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 10.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1546114/mod_resource/content/1/Lecture10.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions of exercises (Session 5).pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1546292/mod_resource/content/1/exercise5-sol.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "25 octobre - 31 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Exercises for Session 6.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1546838/mod_resource/content/1/exercise6.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions of exercises (session 6).pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547070/mod_resource/content/1/exercise6-sol.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Third quiz with solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547071/mod_resource/content/1/quizwithSolutions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Example of Midterm questions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547074/mod_resource/content/1/ExampleMidTermQuestions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Final Exam 2010.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547075/mod_resource/content/1/final2010.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "1 novembre - 7 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 13.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547672/mod_resource/content/2/Lecture13.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions to Example of Midterm Questions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548009/mod_resource/content/1/ExampleMidTermQuestions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Problems Presented by TAs.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548010/mod_resource/content/2/TAPresentation.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 14.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548013/mod_resource/content/1/Lecture14.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 14.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548014/mod_resource/content/1/Lecture14.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "8 novembre - 14 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 15.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548858/mod_resource/content/1/Lecture15.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Midterm exam with solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549058/mod_resource/content/2/MidTermSolutions.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "15 novembre - 21 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 16.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549481/mod_resource/content/1/Lecture16.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercise Sheet 7.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549483/mod_resource/content/1/exercise7.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 17.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549753/mod_resource/content/1/Lecture17.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions to exercise sheet 7.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549833/mod_resource/content/2/exercise7-sol.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "22 novembre - 28 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 18.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550368/mod_resource/content/1/Lecture18.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercise sheet 8.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550369/mod_resource/content/1/exercise8.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 19.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550569/mod_resource/content/2/Lecture19.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Quiz 4 with solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550682/mod_resource/content/1/quizwithsolutions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions to exercise sheet 8.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550906/mod_resource/content/1/exercise8-sol.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "29 novembre - 5 décembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 20.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551221/mod_resource/content/2/Lecture20.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercise sheet 9.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551222/mod_resource/content/1/exercise9.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Solutions to exercise sheet 9.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551614/mod_resource/content/1/exercise9-sol.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Slides of Lecture 21.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551615/mod_resource/content/1/Lecture21.pdf"
                                    }
                                }
                            }
                        }
                    },
                    new Course
                    {
                        Name = "Electronique I",
                        Sections = new[]
                        {
                            new CourseSection
                            {
                                Name = "16 septembre - 22 septembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Cours 00 Introduction à l'électronique.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1539806/mod_resource/content/1/C00EPFL_Introduction.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "23 septembre - 29 septembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Cours Signaux.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1541817/mod_resource/content/1/C01_Signaux%20sans%20commentaires.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Cours signaux avec commentaires.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1541818/mod_resource/content/1/C01_Signaux%20avec%20commentaires.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices signaux.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1541819/mod_resource/content/1/Exo01_Signaux.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices signaux CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1542873/mod_resource/content/1/Exo01_Signaux_Corr.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "30 septembre - 6 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lois de Kirchhoff.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543234/mod_resource/content/1/C02_ApplicKirchhoff.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Lois de Kirchhoff avec commentaires .pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543237/mod_resource/content/1/C02_ApplicKirchhoff%20avec%20commentaires.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Kirchhoff.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543238/mod_resource/content/1/Exo02_ApplicKirchhoff.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Kirchhoff CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543239/mod_resource/content/1/Exo02_ApplicKirchhoff_Corr.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "7 octobre - 13 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Cours Thevenin - Norton.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544344/mod_resource/content/1/C03_MethodesAnalyse.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Cours Thévenin - Norton avec commentaires.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544345/mod_resource/content/1/C03_MethodesAnalyse%20avec%20Comments.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Thévenin - Norton.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544346/mod_resource/content/1/Exo03_MethodesAnalyse.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Thévenin - Norton CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544347/mod_resource/content/1/Exo03_MethodesAnalyse_Corr.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "14 octobre - 20 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Prepa Quiz 01.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544992/mod_resource/content/1/Quiz%20Essai01.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Prepa Quiz 01 CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544993/mod_resource/content/1/Quiz%20Essai01_Correction.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Cours Impédances.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544994/mod_resource/content/1/C04_Impedances.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Cours Impédances avec Commentaires.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544995/mod_resource/content/1/C04_Impedances%20avec%20Comments.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Impedances.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544996/mod_resource/content/1/Exo04_Impedances.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Impédances CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544997/mod_resource/content/1/Exo04_Impedances_Corr.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "21 octobre - 27 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Cours Bode.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545900/mod_resource/content/1/C05_Bode.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Cours Bode avec Commentaires.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545901/mod_resource/content/1/C05_Bode%20avec%20Commentaires.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Bode.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545902/mod_resource/content/1/Exo05_Bode.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Bode CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545903/mod_resource/content/1/Exo05_Bode_Corr.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "28 octobre - 3 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Fonction de transfert pour circuits RLC.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548152/mod_resource/content/1/exercice%20RLC.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "4 novembre - 10 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Cours Impulsions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548062/mod_resource/content/1/C06_Impulsions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Cours Impulsion avec commentaires.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548064/mod_resource/content/1/C06_Impulsions%20avec%20commentaires.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Impulsion.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548065/mod_resource/content/1/Exo06_Impulsions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Impulsions CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548066/mod_resource/content/1/Exo06_Impulsions_Corr.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "11 novembre - 17 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Introduction LTSpice.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549201/mod_resource/content/1/Intro%20LTSPICE.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "calcul valeur résiduelle .pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549365/mod_resource/content/1/valeur%20residuelle.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "18 novembre - 24 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Info quiz 02.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549936/mod_resource/content/1/Info%20quiz%2002.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "exemple quiz.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549937/mod_resource/content/1/Quiz%202%20exemple.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exemple Quiz CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550061/mod_resource/content/1/Quiz%202%20exemple%20CORRECTION.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "25 novembre - 1 décembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "fichiers de simulation.zip",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550645/mod_resource/content/1/Simulation.zip"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Intro Diodes.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550902/mod_resource/content/1/C07_IntroDiodes.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Intro diodes avec Commentaires.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550903/mod_resource/content/1/C07_IntroDiodes%20avec%20Comments.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices diodes.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550904/mod_resource/content/1/Exo07_IntroDiodes.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices diodes CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550905/mod_resource/content/1/Exo07_IntroDiodes_Corr.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "2 décembre - 8 décembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Diodes Partie 2.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551699/mod_resource/content/1/C08_diodes2.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Diodes Partie 2 avec Commentaires.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551700/mod_resource/content/1/C08_diodes2%20avec%20Commentaires.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices diodes 2.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551702/mod_resource/content/1/Exo08_diodes2_.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Exercices Diodes2 CORRECTION.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551703/mod_resource/content/1/Exo08_Diodes2_Corr.pdf"
                                    }
                                }
                            },
                        }
                    },
                    new Course
                    {
                        Name = "Physique Générale I pour IC",
                        Sections = new[]
                        {
                            new CourseSection
                            {
                                Name = "Généralités",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "distribution _TD.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1536793/mod_resource/content/2/distribution_TD2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "ABC du tutorat.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1538334/mod_resource/content/1/ABC_du_tutorat_des_exercices.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "ch0 - introduction.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1539733/mod_resource/content/2/Mecanique_Ch0_2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Mecanique_cours.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1536825/mod_resource/content/4/m%C3%A9canique_2013-14_latest.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Thermodynamique_cours.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548141/mod_resource/content/1/thermo_2013_14.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "16 septembre - 22 septembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD1_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1536785/mod_resource/content/4/TD1_enonce_2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD1_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1536786/mod_resource/content/5/TD1_corrige_2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "mecanique_ch1.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1536791/mod_resource/content/1/Mecanique_ch_1_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "23 septembre - 29 septembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD2_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1536789/mod_resource/content/3/TD2_enonce2013_moodle.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD2_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1536790/mod_resource/content/6/TD2_corrige_2013_rev.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "mecanique_ch2.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1536792/mod_resource/content/1/Mecanique_ch_2_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "30 septembre - 6 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD3_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1542599/mod_resource/content/1/TD3_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD3_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1542601/mod_resource/content/2/TD3_corrige2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "mecanique_ch3.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543461/mod_resource/content/1/Mecanique_ch_3_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "7 octobre - 13 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD4_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543855/mod_resource/content/1/TD4_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD4_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543856/mod_resource/content/2/TD4_corrige2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "14 octobre - 20 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD5_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544941/mod_resource/content/1/TD5_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD5_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544942/mod_resource/content/2/TD5_corrige2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "mecanique_ch4.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1545155/mod_resource/content/1/Mecanique_ch_4_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "21 octobre - 27 octobre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD6_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1546081/mod_resource/content/2/TD6_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD6_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1546082/mod_resource/content/1/TD6_corrige2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "28 octobre - 3 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD7_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1546943/mod_resource/content/1/TD7_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD7_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1546944/mod_resource/content/2/TD7_corrige2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "mecanique_ch5.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547364/mod_resource/content/1/Mecanique_ch_5_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "4 novembre - 10 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD8_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548021/mod_resource/content/1/TD8_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD8_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548022/mod_resource/content/2/TD8_corrige2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "mecanique_ch6.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548024/mod_resource/content/1/Mecanique_ch_6_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "11 novembre - 17 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD9_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548743/mod_resource/content/2/TD9_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD9_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548744/mod_resource/content/2/TD9_corrige2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "mecanique_ch7.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548025/mod_resource/content/1/Mecanique_ch_7_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "18 novembre - 24 novembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD10_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549633/mod_resource/content/1/TD10_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD10_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549634/mod_resource/content/2/TD10_corrige2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "thermodynamique_ch1.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550141/mod_resource/content/1/Thermo_Ch1_2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "thermodynamique_ch2.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550142/mod_resource/content/1/Thermo_Ch2_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "25 novembre - 1 décembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD11_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550488/mod_resource/content/1/TD11_enonce2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "TD11_corrige.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550489/mod_resource/content/2/TD11_corrige2013.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "thermodynamique_ch3.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550866/mod_resource/content/1/Thermo_Ch3_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "2 décembre - 8 décembre",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "TD12_enonce.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550879/mod_resource/content/2/TD12_enonce2013.pdf"
                                    }
                                }
                            },
                        }
                    },
                    new Course
                    {
                        Name = "Circuits et Systèmes I",
                        Sections = new[]
                        {
                            new CourseSection
                            {
                                Name = "Généralités",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Syllabus.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1181211/mod_resource/content/4/syllabus.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Graphical Course Outline.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1507846/mod_resource/content/4/Overview.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Course Pack.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1507866/mod_resource/content/4/polycopy.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Bonus lab.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544604/mod_resource/content/4/bonus_lab.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Bonus lab material.zip",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1544360/mod_resource/content/2/Bonus_lab_material.zip"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Bonus Image Processing Lab.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551534/mod_resource/content/2/Image_Processing_Lab.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Bonus Image Processing Lab material.zip",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551535/mod_resource/content/2/Image%20Processing%20Lab%20files.zip"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "16 September - 20 September",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 1.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148861/mod_resource/content/3/Lecture_Slides/CSI-Lecture%201.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "23 September - 27 September",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 2.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148871/mod_resource/content/2/Lecture_Slides/CSI-Lecture%202%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Lab 1.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1180981/mod_resource/content/3/Labs/Lab01f13.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "SP First MATLAB Toolbox.zip",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1193201/mod_resource/content/0/Labs/spfirst_v153.zip"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "30 September - 4 October",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 3.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148891/mod_resource/content/3/Lecture_Slides/CSI-Lecture%203%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Recitation 1 - Questions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543552/mod_resource/content/3/EE204_2013_REC_01.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "CSI Lecture 3: matlab example.m",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1543568/mod_resource/content/1/CSI_example.m"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "7 October - 11 October",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 4.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148911/mod_resource/content/2/Lecture_Slides/CSI-Lecture%204%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Lab 2.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1507797/mod_resource/content/5/Lab02f13.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "14 October - 18 October",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 5.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148921/mod_resource/content/2/Lecture_Slides/CSI-Lecture%205%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Lab 3.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1181011/mod_resource/content/7/Labs/Lab03f13.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "21 October - 25 October",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 6.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148931/mod_resource/content/2/Lecture_Slides/CSI-Lecture%206%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Recitation 2 - questions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1546453/mod_resource/content/1/EE204_2013_REC_02.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "28 October - 1 November",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 7.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148941/mod_resource/content/1/Lecture_Slides/CSI-Lecture%207%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Lab 4.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1302521/mod_resource/content/7/Labs/Lab04f13.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Homework 6.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547489/mod_resource/content/2/EE204_HW6_2013.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "4 November - 8 November",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Review problems.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1265561/mod_resource/content/0/recitation/review_session_problems.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "2011 Bonus Midterm.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1512218/mod_resource/content/1/CSI_2011_Bonus_Midterm.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "2011 Bonus Midterm Solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1512219/mod_resource/content/1/CSI_2011_Bonus_Midterm_Solutions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "2012 Bonus Midterm.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547607/mod_resource/content/1/CSI_2012_Bonus_Midterm.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "2012 Bonus Midterm Solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1547609/mod_resource/content/1/CSI_2012_Bonus_Midterm_Solutions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "EE204 2013 midterm review.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548388/mod_resource/content/1/EE204_2013_midterm_review.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "EE204 2013 midterm review solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1548591/mod_resource/content/2/EE204_2013_midterm_review_solutions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "EE204 2013 Bonus Midterm Exam.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549333/mod_resource/content/2/EE204_CSI_2013_Bonus_Midterm.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "EE204 2013 Bonus midterm exam solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1549334/mod_resource/content/3/EE204_midterm_exam_solutions.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "18 November - 22 November",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 10.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148971/mod_resource/content/3/Lecture_Slides/CSI-Lecture%2010%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Recitation 3 problem.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550146/mod_resource/content/1/EE204_2013_REC_03.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Recitation 3 problem with solution.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550239/mod_resource/content/1/EE204_2013_REC_03.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "25 November - 29 November",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 11.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1148981/mod_resource/content/2/Lecture_Slides/CSI-Lecture%2011%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Recitation problems.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550960/mod_resource/content/2/EE204_2013_REC_04.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Homework 8.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1550961/mod_resource/content/1/EE204_2013_HW8.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Recitation problems with solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551107/mod_resource/content/2/EE204_2013_REC_04.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "2 December - 6 December",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 12.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1507856/mod_resource/content/3/CSI-Lecture%2012%20%282013%29.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Homework 9.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551684/mod_resource/content/1/EE204_2013_HW9.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Recitation problems.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551765/mod_resource/content/2/EE204_2013_REC_05.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "Recitation solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1551834/mod_resource/content/1/EE204_2013_REC_05_solutions.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "9 December - 13 December",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Lecture Slides - Week 13.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1507865/mod_resource/content/2/CSI-Lecture%2013%20%282013%29.pdf"
                                    }
                                }
                            },
                            new CourseSection
                            {
                                Name = "16 December - 20 December",
                                Files = new[]
                                {
                                    new CourseFile
                                    {
                                        Name = "Review Session Problems.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1517771/mod_resource/content/1/Review_session.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "2011 Final Exam.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1517901/mod_resource/content/1/CSI_2011_Final_Eam.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "2011 Final Exam Solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1517902/mod_resource/content/1/CSI_2011_Final_Exam_Solutions.pdf"
                                    },
                                    new CourseFile
                                    {
                                        Name = "2012 Final Exam Solutions.pdf",
                                        Url = "http://moodle.epfl.ch/pluginfile.php/1518760/mod_resource/content/1/CSI_Final_Exam_2012_Solutions.pdf"
                                    }
                                }
                            }
                        }
                    }
                };
            }
        }
#endif
    }
}