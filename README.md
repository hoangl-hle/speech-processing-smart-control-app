# speech-processing-smart-control-app
Ex speech processing at university - smart control app for smart home

- Link github chứa code của phần ứng dụng app android: https://github.com/hoangle98/speech-processing-smart-control-app
(Còn về code HomeGateway ( IoT platform sẽ được nhắc ở dưới) do là sản phẩm của nhóm IoTLab bọn em nên không tiện gửi link code ạ)
- Mô tả dự án: Phần mềm cho điện thoại android, ứng dụng điều khiển điều hòa trong nhà thông minh bằng giọng nói, sử dụng nền tảng IoTplatform của nhóm IoT lab của bọn em tự phát triển bằng OpenEcho, ở bài tập lần này em sẽ sử dụng mô hình cơ bản sau để để demo dự án: 

Ứng dụng giao tiếp với ứng dụng HomeGateway của IoTplatform bằng giao thức MQTT
HomeGateway giao tiếp với thiết bị (điều hòa) trong nhà thông minh bằng giao thức Echonet lite em đã tìm hiểu trong quá trình tham gia nghiên cứu ở lab ( giao thức của các thiết bị thông minh được các công ty Nhật sử dụng). 
Ở đây em sẽ giả lập nhà thông minh bằng  MoekadenRoom (https://github.com/SonyCSL/MoekadenRoom) của MIT Sony để demo sản phẩm. 
Người dùng ra lệnh cho "Robot" có tên là "Android" bằng khẩu lệnh "Android" kèm với Mệnh lệnh đi kèm. Mệnh lệnh đi kèm có thể là :
Bật điều hoà
Tắt điều hoà
Bật điều hoà và chuyển sang chế độ làm lạnh làm nóng , gió , làm khô, tự động,..
Chuyển sang chế độ làm lạnh, nóng, khô, gió tự động.
Tăng nhiệt độ
Giảm nhiệt độ
--> Trong lúc ra lệnh cho ứng dụng bằng giọng nói, đồng thời ứng dụng cũng sẽ phản hồi lại với nguời dùng bằng giọng nữ có tên là "Android". Trả lời khi đã tiếp nhận yêu cầu, và thực hiện yeu cầu của nguời dùng., khi ra lệnh thất bại, khi cô ấy không hiểu lệnh nguời dùng, giới thiệu mình,....
Lệnh yêu cầu từ nguời dùng về các chức năng ở trên không nhất thiết phải đúng theo y hệt cấu trúc một câu nào mà có thế là một số câu tương tự (Em xử lí bằng cách bắt các hot key từ quan trọng rồi lại bắt tiếp các hot key khác liên quan đến nó ... từ đó suy ra lệnh)
- Link youtube demo: https://youtu.be/HvillaKcLvc ( demo có kèm giả lập điều hoà trong ngôi nhà thông minh)
(Bổ sung: videohttps://www.youtube.com/watch?v=Zdbz-gcTUx4 này có thêm lệnh vừa yêu cầu bật điều hoà lẫn chuyển chế độ. mà ở video ở trên không có)
-Em không đăng kèm code Homegateway~IoTplatform vì đây là sản phẩm của nhóm lab IoT em thực hiện. nhưng có chạy kèm code demo trong video đầu tiên
