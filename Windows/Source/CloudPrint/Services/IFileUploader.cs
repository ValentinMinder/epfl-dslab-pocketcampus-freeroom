using System.IO;
using System.Threading.Tasks;

namespace PocketCampus.CloudPrint.Services
{
    public interface IFileUploader
    {
        Task<long> UploadFileAsync( Stream file );
    }
}