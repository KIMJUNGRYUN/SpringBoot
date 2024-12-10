package mysite.expense.service;

import lombok.RequiredArgsConstructor;
import mysite.expense.dto.ExpenseDTO;
import mysite.expense.dto.ExpenseFilterDTO;
import mysite.expense.entity.Expense;
import mysite.expense.repository.ExpenseRepository;
import mysite.expense.util.DateTimeUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor // final 이 붙은 필드를 생성자로 만들어줌
// 생성자 주입을 해줌, lombok 이용해서 하는법
public class ExpenseService {


    private final ExpenseRepository expRepo;
    private final ModelMapper modelMapper;
    
    // alt + insert 로 생성자 만듬, @Autowired 사용하지않고 생성자 사용으로 처리
    // 생성자 주입 , 이 방법을 권장
   /* public ExpenseService(ExpenseRepository expRepo) {
        this.expRepo = expRepo;
    } */

    // 모든 비용 리스트를 가져옴
    public List<ExpenseDTO> getAllExpenses () {
        List<Expense> list = expRepo.findAll();
        List<ExpenseDTO> listDTO = list.stream() // 스트림으로 변환
                                    .map(this::mapToDTO) // mapToDTO 로 모두 변환됨
                                 // .map(e->mapToDTO(e)) 람다식
                                    .collect(Collectors.toList()); // 다시 리스트로
        return listDTO;
    }
    // 엔티티 => DTO 변환 (값을 전달)
    private ExpenseDTO mapToDTO(Expense expense) {
       /* ExpenseDTO expenseDTO = new ExpenseDTO(); // 빈객체
        expenseDTO.setId(expense.getId());
        expenseDTO.setExpenseId(expense.getExpenseId());
        expenseDTO.setAmount(expense.getAmount());
        expenseDTO.setName(expense.getName());
        expenseDTO.setDescription(expense.getDescription());
        expenseDTO.setDate(expense.getDate());*/
        ExpenseDTO expenseDTO = modelMapper.map(expense, ExpenseDTO.class);
        expenseDTO.setDateString(DateTimeUtil.convertDateToString(expenseDTO.getDate()));
        return expenseDTO;
    }

    public ExpenseDTO saveExpenseDetails(ExpenseDTO expenseDTO) throws ParseException {
        //1. DTO => Entity
        Expense expense = mapToEntity(expenseDTO);
        //2. DB에 저장
        expense = expRepo.save(expense);
        //3. Entity => DTO
        return mapToDTO(expense);
    }

    private Expense mapToEntity(ExpenseDTO expenseDTO) throws ParseException {
        Expense expense = modelMapper.map(expenseDTO, Expense.class);
        //1. expenseID 입력 (유니크 문자열 자동생성), 업데이트 일경우 아이디를 만들지 않는다.
        if (expenseDTO.getId() == null) {
            expense.setExpenseId(UUID.randomUUID().toString());
        }

        //2. date 입력 ("2024-12-17" => sql Date 타입으로 변경)
        expense.setDate(DateTimeUtil.convertStringToDate(expenseDTO.getDateString()));
        return expense;
    }

    //비용 id(ExpenseId)로 삭제하기
    public void deleteExpense(String id) {
        Expense expense = expRepo.findByExpenseId(id).orElseThrow(()->
                new RuntimeException("해당 ID의 아이템을 찾을 수 없습니다"));
        expRepo.delete(expense);
    }

    //expenseId로 수정할 expense 찾아서 DTO 변환하여 리턴
    public ExpenseDTO getExpenseById(String id) {
        Expense expense = expRepo.findByExpenseId(id).orElseThrow(
                ()-> new RuntimeException("해당 ID의 비용을 찾을 수 없습니다."));
        /* ExpenseDTO expenseDTO = mapToDTO(expense); */
        return mapToDTO(expense); // DTO 변환
    }

    public List<ExpenseDTO> getFilterExpenses(ExpenseFilterDTO filter) throws ParseException {
        String keyword = filter.getKeyword();
        String sortBy = filter.getSortBy();
        String startDate = filter.getStartDate();
        String endDate = filter.getEndDate();
        // sql 날짜로 변경 (문자열 시작일 종료일을)
        Date startDay = !startDate.isEmpty() ? DateTimeUtil.convertStringToDate(startDate) : new Date(0);
        Date endDay = !endDate.isEmpty() ? DateTimeUtil.convertStringToDate(endDate) : new Date(System.currentTimeMillis()) ;
        List<Expense> list = expRepo.findByNameContainingAndDateBetween(keyword, startDay, endDay);
        List<ExpenseDTO> filterList = list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        // 날짜 또는 가격으로 정렬
        if (sortBy.equals("date")) {
            filterList.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        } else{
            filterList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
        }
        return filterList;
    }

    // 리스트의 총비용 합계
    public Long totalExpenses(List<ExpenseDTO> expenses){
        Long sum = expenses.stream()
                // 객체를 숫자로 바꿈 객체 > 가격으로
                // .mapToLong(x->x.getAmount()) 람다식
                // map 은 원래를 다음나오는걸(???)로 변경 , to ???
                .mapToLong(ExpenseDTO::getAmount)
                .sum();
        return sum;
    }



}
