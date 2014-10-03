// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for ViewModels

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

        public Design()
        {
            var moodleService = new DesignMoodleService();
            var course = moodleService.GetCoursesAsync( null, CancellationToken.None ).Result.Courses[0];

            Course = new CourseViewModel( new DesignDataCache(), new DesignSecureRequestHandler(), moodleService,
                                          new DesignMoodleDownloader(), new DesignFileStorage(), new DesignBrowserService(),
                                          course );
            Main = new MainViewModel( new DesignDataCache(), new DesignSecureRequestHandler(), new DesignNavigationService(), new DesignMoodleService() );

            Course.OnNavigatedToAsync();
            Main.OnNavigatedToAsync();
        }
#endif
    }
}