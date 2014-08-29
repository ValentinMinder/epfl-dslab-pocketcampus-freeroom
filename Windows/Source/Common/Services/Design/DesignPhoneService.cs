#if DEBUG
namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignPhoneService : IPhoneService
    {
        public bool CanCall
        {
            get { return false; }
        }

        public void Call( string name, string number ) { }
    }
}
#endif