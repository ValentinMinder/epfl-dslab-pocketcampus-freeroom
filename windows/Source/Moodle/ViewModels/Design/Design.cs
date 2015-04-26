// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

using PocketCampus.Moodle.Models;
#if DEBUG
using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.Moodle.Services.Design;
using ThinMvvm.Design;
#endif

namespace PocketCampus.Moodle.ViewModels.Design
{
    public sealed class Design
    {
#if DEBUG
        public CourseViewModel Course { get; private set; }
        public MainViewModel Main { get; private set; }
        public FileViewModel File { get; private set; }

        public Design()
        {
            var moodleService = new DesignMoodleService();
            var course = moodleService.GetCoursesAsync( null, CancellationToken.None ).Result.Courses[0];
            var file = new MoodleFile { Name = "Example", Extension = "pdf", PathComponents = new[] { "Course", "Folder" } };

            Course = new CourseViewModel( new DesignDataCache(), new DesignSecureRequestHandler(), new DesignNavigationService(),
                                          moodleService, new DesignBrowserService(),
                                          course );
            Main = new MainViewModel( new DesignDataCache(), new DesignSecureRequestHandler(), new DesignNavigationService(), new DesignMoodleService() );
            File = new FileViewModel( moodleService, new DesignMoodleDownloader(), new DesignFileStorage(), file );

            Course.OnNavigatedToAsync();
            Main.OnNavigatedToAsync();
            File.OnNavigatedToAsync();
        }
#endif
    }
}