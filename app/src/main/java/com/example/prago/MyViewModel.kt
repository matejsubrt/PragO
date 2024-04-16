import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prago.ConnectionSearchResult

class SharedViewModel : ViewModel() {
    var searchResult = MutableLiveData<ConnectionSearchResult>()
}