using PocketCampus.Camipro.Services.Design;
using PocketCampus.Camipro.ViewModels;
using PocketCampus.Common.Services.Design;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Camipro.Views.Design
{
    public sealed class DesignMainViewModel : DesignViewModel<MainViewModel, NoParameter>
    {
#if DEBUG
        protected override MainViewModel ViewModel
        {
            get { return new MainViewModel( new DesignCamiproService(), new DesignSecureRequestHandler() ); }
        }
#endif
    }
}