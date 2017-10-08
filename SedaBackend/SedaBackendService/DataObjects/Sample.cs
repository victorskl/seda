using Microsoft.Azure.Mobile.Server;

namespace SedaBackendService.DataObjects
{
    public class Sample : EntityData
    {
        public string text { get; set; }

        public bool complete { get; set; }

        public long startTime { get; set; }

        public long endTime { get; set; }

        public int count { get; set; }

        public int sampleType { get; set; }
    }
}