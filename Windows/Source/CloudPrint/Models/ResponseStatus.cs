using ThriftSharp;

namespace PocketCampus.CloudPrint.Models
{
    [ThriftEnum]
    public enum ResponseStatus
    {
        Success = 200,
        AuthenticationError = 407,
        PrintError = 404
    }
}